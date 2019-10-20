// Copyright 2011 David Galles, University of San Francisco. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, are
// permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this list of
// conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice, this list
// of conditions and the following disclaimer in the documentation and/or other materials
// provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ``AS IS'' AND ANY EXPRESS OR IMPLIED
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
// ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those of the
// authors and should not be interpreted as representing official policies, either expressed
// or implied, of the University of San Francisco

package com.tvd12.algorithm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BTree {

	protected BTreeNode treeRoot;
	protected final int max_keys;
	protected final int max_degree;
	protected final int min_keys;
	protected final int split_index;
	protected final boolean preemptiveSplit;
	
	public BTree(int degree) {
		this.treeRoot = null;
		int newDegree = degree;
		this.max_degree = newDegree;
		this.max_keys = newDegree - 1;
		this.preemptiveSplit = false;
		this.min_keys = (int) (Math.floor((newDegree + 1) / 2.0D) - 1);
		this.split_index = (int) Math.floor((newDegree - 1) / 2.0D);
	}
	
	int currentInsertedValue;
	public void insert(int insertedValue) {
		currentInsertedValue = insertedValue;
		if (this.treeRoot == null) {
			this.treeRoot = new BTreeNode(max_degree);
			this.treeRoot.keys[0] = insertedValue;
		}
		else
		{
			if (this.preemptiveSplit)
			{
				if (this.treeRoot.numKeys == this.max_keys)
				{
					this.split(this.treeRoot);
					this.resizeTree();
					
				}
				this.insertNotFull(this.treeRoot, insertedValue);				
			}
			else
			{
				this.insert(this.treeRoot, insertedValue);					
			}
		}
		
	}
	
	private void insert(BTreeNode tree, int insertValue) {
		if (tree.isLeaf)
		{
			tree.numKeys++;
			int insertIndex = tree.numKeys - 1;
			while (insertIndex > 0 && tree.keys[insertIndex - 1] > insertValue)
			{
				tree.keys[insertIndex] = tree.keys[insertIndex - 1];
				insertIndex--;
			}
			tree.keys[insertIndex] = insertValue;
			this.resizeTree();
			this.insertRepair(tree);
		}
		else
		{
			int findIndex = 0;
			while (findIndex < tree.numKeys && tree.keys[findIndex] < insertValue)
			{
				findIndex++;					
			}				
			this.insert(tree.children[findIndex], insertValue);				
		}
	}
	
	private void insertRepair(BTreeNode tree) {
		if (tree.numKeys <= this.max_keys)
		{
			return;
		}
		else if (tree.parent == null)
		{
			this.treeRoot = this.split(tree);
			return;
		}
		else
		{
			BTreeNode newNode  = this.split(tree);
			this.insertRepair(newNode);
		}
	}
	
	private void insertNotFull(BTreeNode tree, int insertValue) {
		if (tree.isLeaf)
		{
			tree.numKeys++;
			int insertIndex = tree.numKeys - 1;
			while (insertIndex > 0 && tree.keys[insertIndex - 1] > insertValue)
			{
				tree.keys[insertIndex] = tree.keys[insertIndex - 1];
				insertIndex--;
			}
			tree.keys[insertIndex] = insertValue;
			this.resizeTree();
		}
		else
		{
			int findIndex = 0;
			while (findIndex < tree.numKeys && tree.keys[findIndex] < insertValue)
			{
				findIndex++;					
			}				
			if (tree.children[findIndex].numKeys == this.max_keys)
			{
				BTreeNode newTree = this.split(tree.children[findIndex]);
				this.resizeTree();
				this.insertNotFull(newTree, insertValue);
			}
			else
			{
				this.insertNotFull(tree.children[findIndex], insertValue);
			}
		}
	}
	
	private BTreeNode split(BTreeNode tree) {
		BTreeNode rightNode = new BTreeNode(max_degree);
		rightNode.numKeys = tree.numKeys - this.split_index - 1;
		int risingNode = tree.keys[this.split_index];
		
		
		if (tree.parent != null)
		{
			BTreeNode currentParent = tree.parent;
			int parentIndex = 0;
			for (; parentIndex < currentParent.numKeys + 1 
						&& currentParent.children[parentIndex] != tree; 
					parentIndex++);
			if (parentIndex == currentParent.numKeys + 1)
			{
				throw new Error("Couldn't find which child we were!");
			}
			for (int i = currentParent.numKeys; i > parentIndex; i--)
			{
				currentParent.children[i+1] = currentParent.children[i];
				currentParent.keys[i] = currentParent.keys[i-1];
			}
			currentParent.numKeys++;
			currentParent.keys[parentIndex] = risingNode;
			
			currentParent.children[parentIndex+1] = rightNode;
			rightNode.parent = currentParent;
		}
		
		for (int i = this.split_index + 1; i < tree.numKeys + 1; i++)
		{
			rightNode.children[i - this.split_index - 1] = tree.children[i];
			if (tree.children[i] != null)
			{
				rightNode.isLeaf = false;
				if (tree.children[i] != null) {
					tree.children[i].parent = rightNode;
				}
				tree.children[i] = null;
				
			}
		}
		for (int i = this.split_index+1; i < tree.numKeys; i++)
		{
			rightNode.keys[i - this.split_index - 1] = tree.keys[i];
		}
		
		BTreeNode leftNode = tree;
		leftNode.numKeys = this.split_index;
		
		if (tree.parent != null)
		{
			return tree.parent;
		}
		else //			if (tree.parent == null)
		{
			this.treeRoot = new BTreeNode(max_degree);
			this.treeRoot.keys[0] = risingNode;
			this.treeRoot.children[0] = leftNode;
			this.treeRoot.children[1] = rightNode;
			leftNode.parent = this.treeRoot;
			rightNode.parent = this.treeRoot;
			this.treeRoot.isLeaf = false;
			return this.treeRoot;
		}
	}
	
	AtomicInteger resizeCount = new AtomicInteger();
	private void resizeTree() {
		//TODO
		System.out.println("resizeTree: " + resizeCount.incrementAndGet() + ", currentInsertedValue = " + currentInsertedValue); 
//		System.out.println(this);
	}
	
	public void delete(int deletedValue) {
		if (this.preemptiveSplit)
		{
			this.doDeleteNotEmpty(this.treeRoot, deletedValue);
		}
		else
		{
			this.doDelete(this.treeRoot, deletedValue);
			
		}
		if (this.treeRoot.numKeys == 0)
		{
			this.treeRoot = this.treeRoot.children[0];
			this.treeRoot.parent = null;
			this.resizeTree();
		}
	}
	
	private void doDeleteNotEmpty(BTreeNode tree, int val) {
		if (tree != null)
		{
			int i;
			for (i = 0; i < tree.numKeys && tree.keys[i] < val; i++);
			if (i == tree.numKeys)
			{
				if (!tree.isLeaf)
				{
					if (tree.children[tree.numKeys].numKeys == this.min_keys)
					{
						BTreeNode nextNode;
						if (tree.children[tree.numKeys - 1].numKeys > this.min_keys)
						{
							nextNode = this.stealFromLeft(tree.children[tree.numKeys], tree.numKeys);
							this.doDeleteNotEmpty(nextNode, val);
						}
						else
						{
							nextNode = this.mergeRight(tree.children[tree.numKeys - 1]);
							this.doDeleteNotEmpty(nextNode, val);
						}
					}
					else
					{
						this.doDeleteNotEmpty(tree.children[tree.numKeys], val);							
					}
				}
			}
			else if (tree.keys[i] > val)
			{
				if (!tree.isLeaf)
				{
					if (tree.children[i].numKeys > this.min_keys)
					{
						this.doDeleteNotEmpty(tree.children[i], val);
					}
					else
					{
						if (tree.children[i+1].numKeys > this.min_keys)
						{
							BTreeNode nextNode = this.stealFromRight(tree.children[i], i);
							this.doDeleteNotEmpty(nextNode, val);
						}
						else
						{
							BTreeNode nextNode = this.mergeRight(tree.children[i]);
							this.doDeleteNotEmpty(nextNode, val);
						}
						
					}
				}
			}
			else
			{
				if (tree.isLeaf)
				{
					for (int j = i; j < tree.numKeys - 1; j++)
					{
						tree.keys[j] = tree.keys[j+1];
					}
					tree.numKeys--;
					this.resizeTree();
				}
				else
				{
					BTreeNode maxNode = tree.children[i];
					
					if (tree.children[i].numKeys == this.min_keys)
					{
						// Trees to left and right of node to delete don't have enough keys
						//   Do a merge, and then recursively delete the element
						if (tree.children[i+1].numKeys == this.min_keys)
						{
							BTreeNode nextNode = this.mergeRight(tree.children[i]);
							this.doDeleteNotEmpty(nextNode, val);
							return;
						}
						else
						{
							BTreeNode minNode = tree.children[i+1];
							while (!minNode.isLeaf)
							{
								if (minNode.children[0].numKeys == this.min_keys)
								{
									if (minNode.children[1].numKeys == this.min_keys)
									{
										minNode = this.mergeRight(minNode.children[0]);
									}
									else
									{
										minNode = this.stealFromRight(minNode.children[0], 0);
									}
								}
								else
								{
									minNode = minNode.children[0];
								}
							}
							
							tree.keys[i] = minNode.keys[0];
							for (i = 1; i < minNode.numKeys; i++)
							{
								minNode.keys[i-1] = minNode.keys[i];
							}
							
							minNode.numKeys--;
							this.resizeTree();
							
						}
					}
					else
					{
						
						while (!maxNode.isLeaf)
						{
							if (maxNode.children[maxNode.numKeys].numKeys == this.min_keys)
							{
								if (maxNode.children[maxNode.numKeys - 1].numKeys > this.min_keys)
								{
									maxNode = this.stealFromLeft(maxNode.children[maxNode.numKeys], maxNode.numKeys);
								}
								else
								{
									
								}	maxNode = this.mergeRight(maxNode.children[maxNode.numKeys-1]);
							}
							else
							{
								maxNode = maxNode.children[maxNode.numKeys];
							}
						}
						tree.keys[i] = maxNode.keys[maxNode.numKeys - 1];
						maxNode.numKeys--;
						this.resizeTree();
					}
					
				}
			}
			
		}
	}
	
	private void doDelete(BTreeNode tree, int val) {
		if (tree != null)
		{
			int i;
			for (i = 0; i < tree.numKeys && tree.keys[i] < val; i++);
			if (i == tree.numKeys)
			{
				if (!tree.isLeaf)
				{
					this.doDelete(tree.children[tree.numKeys], val);
				}
			}
			else if (tree.keys[i] > val)
			{
				if (!tree.isLeaf)
				{
					this.doDelete(tree.children[i], val);
				}
			}
			else
			{
				if (tree.isLeaf)
				{
					for (int j = i; j < tree.numKeys - 1; j++)
					{
						tree.keys[j] = tree.keys[j+1];
					}
					tree.numKeys--;
					this.repairAfterDelete(tree);
				}
				else
				{
					BTreeNode maxNode = tree.children[i];
					while (!maxNode.isLeaf)
					{
						maxNode = maxNode.children[maxNode.numKeys];
					}
					tree.keys[i] = maxNode.keys[maxNode.numKeys - 1];
					maxNode.numKeys--;
					this.repairAfterDelete(maxNode);					
				}
			}
			
		}
	}
	
	private void repairAfterDelete(BTreeNode tree) {
		if (tree.numKeys < this.min_keys)
		{
			if (tree.parent == null)
			{
				if (tree.numKeys == 0)
				{
					this.treeRoot = tree.children[0];
					if (this.treeRoot != null)
						this.treeRoot.parent = null;
					this.resizeTree();
				}
			}
			else
			{
				BTreeNode parentNode = tree.parent;
				int parentIndex = 0;
				for (; parentNode.children[parentIndex] != tree; parentIndex++);
				if (parentIndex > 0 && parentNode.children[parentIndex - 1].numKeys > this.min_keys)
				{
					this.stealFromLeft(tree, parentIndex);
					
				}
				else if (parentIndex < parentNode.numKeys && parentNode.children[parentIndex + 1].numKeys > this.min_keys)
				{
					this.stealFromRight(tree,parentIndex);
					
				}
				else if (parentIndex == 0)
				{
					// Merge with right sibling
					BTreeNode nextNode = this.mergeRight(tree);
					this.repairAfterDelete(nextNode.parent);			
				}
				else
				{
					// Merge with left sibling
					BTreeNode nextNode = this.mergeRight(parentNode.children[parentIndex-1]);
					this.repairAfterDelete(nextNode.parent);			
					
				}
				
				
			}
		}
	}
	
	private BTreeNode stealFromLeft(BTreeNode tree, int parentIndex) {
		BTreeNode parentNode = tree.parent;
		// Steal from left sibling
		tree.numKeys++;
		
		for (int i = tree.numKeys - 1; i > 0; i--)
		{
			tree.keys[i] = tree.keys[i-1];
		}
		BTreeNode leftSib = parentNode.children[parentIndex -1];
		
		if (!tree.isLeaf)
		{
			for (int i = tree.numKeys; i > 0; i--)
			{
				tree.children[i] =tree.children[i-1];
			}
			tree.children[0] = leftSib.children[leftSib.numKeys];
			leftSib.children[leftSib.numKeys] = null;
			tree.children[0].parent = tree;
			
		}
		
		tree.keys[0] = parentNode.keys[parentIndex - 1];
		parentNode.keys[parentIndex-1] = leftSib.keys[leftSib.numKeys - 1];
		
		leftSib.numKeys--;
		this.resizeTree();
		return tree;
	}
	
	private BTreeNode stealFromRight(BTreeNode tree, int parentIndex) {
		// Steal from right sibling
		BTreeNode parentNode = tree.parent;
		
		BTreeNode rightSib = parentNode.children[parentIndex + 1];
		tree.numKeys++;
		tree.keys[tree.numKeys - 1] = parentNode.keys[parentIndex];
		parentNode.keys[parentIndex] = rightSib.keys[0];
		if (!tree.isLeaf)
		{
			tree.children[tree.numKeys] = rightSib.children[0];
			tree.children[tree.numKeys].parent = tree;
			for (int i = 1; i < rightSib.numKeys + 1; i++)
			{
				rightSib.children[i-1] = rightSib.children[i];
			}
			
		}
		for (int i = 1; i < rightSib.numKeys; i++)
		{
			rightSib.keys[i-1] = rightSib.keys[i];
		}
		rightSib.numKeys--;
		this.resizeTree();
		return tree;
	}
	
	private BTreeNode mergeRight(BTreeNode tree) {
		BTreeNode parentNode = tree.parent;
		int parentIndex = 0;
		for (parentIndex = 0; parentNode.children[parentIndex] != tree; parentIndex++);
		BTreeNode rightSib = parentNode.children[parentIndex+1];
		
		tree.keys[tree.numKeys] = parentNode.keys[parentIndex];
		
		for (int i = 0; i < rightSib.numKeys; i++)
		{
			tree.keys[tree.numKeys + 1 + i] = rightSib.keys[i];
		}
		if (!tree.isLeaf)
		{
			for (int i = 0; i <= rightSib.numKeys; i++)
			{
				tree.children[tree.numKeys + 1 + i] = rightSib.children[i];
				tree.children[tree.numKeys + 1 + i].parent = tree;
			}
		}
		for (int i = parentIndex+1; i < parentNode.numKeys; i++)
		{
			parentNode.children[i] = parentNode.children[i+1];
			parentNode.keys[i-1] = parentNode.keys[i];
		}
		parentNode.numKeys--;
		tree.numKeys = tree.numKeys + rightSib.numKeys + 1;
		return tree;
	}
	
	public Integer find(int findValue) {
		return this.findInTree(this.treeRoot, findValue);
	}
	
	private Integer findInTree(BTreeNode tree, int val) {
		if (tree != null) {
			int i;
			for (i = 0; i < tree.numKeys && tree.keys[i] < val; i++);
			if (i == tree.numKeys) {
				if (!tree.isLeaf)
					return this.findInTree(tree.children[tree.numKeys], val);
				else {
					return null;
				}
			}
			else if (tree.keys[i] > val) {
				if (!tree.isLeaf)
					return this.findInTree(tree.children[i], val);
				else
					return null;
			}
			else
				return val;
		}
		else {
			return null;
		}
	}
	
	public List<Integer> printTree() {
		List<Integer> out = new ArrayList<>();
		printTree(out);
		return out;
	}
	
	private void printTree(List<Integer> out) {
		this.printTreeRec(this.treeRoot, out);
	}
	
	private void printTreeRec(BTreeNode tree, List<Integer> out) {
		if(tree.isLeaf) {
			for(int i = 0; i < tree.numKeys ; ++i) {
				out.add(tree.keys[i]);
			}
		}
		else {
			printTreeRec(tree.children[0], out);
			for(int i = 0; i < tree.numKeys ; ++i) {
				out.add(tree.keys[i]);
				printTreeRec(tree.children[i + 1], out);
			}
		}
	}
	
	public void clear() {
		this.deleteTree(treeRoot);
		this.treeRoot = null;
	}
	
	private void deleteTree(BTreeNode tree) {
		if (tree != null)
		{
			if (!tree.isLeaf)
			{
				for (int i = 0; i <= tree.numKeys; i++)
				{
					this.deleteTree(tree.children[i]);
					tree.children[i].clear();
				}
			}
		}
	}
	
	private static class BTreeNode {
		
		int numKeys;
		boolean isLeaf;
		BTreeNode parent;
		final Integer[] keys;
		final BTreeNode[] children;
		
		BTreeNode(int degree) {
			this.keys = new Integer[degree];
			this.children = new BTreeNode[degree + 1];
			this.numKeys = 1;
			this.isLeaf = true;
			this.parent = null;
		}
		
		void toString(List<List<BTreeNode>> all, int index) {
			List<BTreeNode> list = new ArrayList<>();
			for(BTreeNode child : children) {
				if(child == null)
					continue;
				list.add(child);
			}
			if(list.isEmpty())
				return;
			List<BTreeNode> current = null;
			if(all.size() > index) {
				current = all.get(index);
			}
			else {
				current = new ArrayList<>();
				all.add(current);
			}
			current.addAll(list);
			for(BTreeNode child : list) {
				child.toString(all, index + 1);
			}
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for(int i = 0 ; i < numKeys ; ++i) {
				builder.append(keys[i]);
				if(i < numKeys - 1)
					builder.append(" ");
			}
			return builder.toString();
		}
		
		public void clear() {
			// TODO: clear
		}
	}
	
	@Override
	public String toString() {
		List<List<BTreeNode>> all = new ArrayList<>();
		all.add(Arrays.asList(treeRoot));
		treeRoot.toString(all, 1);
		StringBuilder builder = new StringBuilder();
		for(List<BTreeNode> list : all) {
			for(BTreeNode node : list) {
				builder.append(node).append(" " );
			}
			builder.append("\n");
		}
		return builder.toString();
	}
	
	public static void main(String[] args) {
		BTree tree = new BTree(3);
		tree.insert(1);
		tree.insert(2);
		tree.insert(3);
		tree.insert(4);
		tree.insert(5);
		tree.insert(6);
		tree.insert(7);
//		System.out.println(tree);
//		System.out.println(tree.find(7) == null ? "not found" : "found");
//		System.out.println("printTree: " + tree.printTree());
		tree.delete(2);
		System.out.println(tree);
//		System.out.println(tree.find(7) == null ? "not found" : "found");
	}
}
