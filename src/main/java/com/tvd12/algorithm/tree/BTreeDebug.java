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

public class BTreeDebug {

	protected BTreeNode treeRoot;
	protected final int max_keys;
	protected final int max_degree;
	protected final int min_keys;
	protected final int split_index;
	
	public BTreeDebug(int degree) {
		this.treeRoot = null;
		int newDegree = degree;
		this.max_degree = newDegree;
		this.max_keys = newDegree - 1;
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
			this.insert(this.treeRoot, insertedValue);					
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
			tree.keys[i] = null;
		}
		
		BTreeNode leftNode = tree;
		leftNode.keys[this.split_index] = null;
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
//		System.out.println("resizeTree: " + resizeCount.incrementAndGet()); 
//		System.out.println(this);
	}
	
	public void delete(int deletedValue) {
		this.doDelete(this.treeRoot, deletedValue);
			
		if (this.treeRoot.numKeys == 0)
		{
			this.treeRoot = this.treeRoot.children[0];
			this.treeRoot.parent = null;
			this.resizeTree();
		}
	}
	
	private final AtomicInteger doDeleteCount = new AtomicInteger();
	private void doDelete(BTreeNode tree, int val) {
		int count = doDeleteCount.incrementAndGet();
		System.out.println("============ start do delete: " + count + " =========");
		System.out.println("tree: " + tree + ", val: " + val + ", current:\n" + this);
		if (tree != null)
		{
			int i;
			for (i = 0; i < tree.numKeys && tree.keys[i] < val; i++);
			System.out.println("1. i: " + i);
			if (i == tree.numKeys)
			{
				System.out.println("2. i == tree.numKeys" + i + " === " + tree.numKeys);
				if (!tree.isLeaf)
				{
					System.out.println("3. !tree.isLeaf");	
					this.doDelete(tree.children[tree.numKeys], val);
				}
			}
			else if (tree.keys[i] > val)
			{
				System.out.println("4. tree.keys[i] > val: " + tree.keys[i] + " > " + val);
				if (!tree.isLeaf)
				{
					System.out.println("5. !tree.isLeaf");
					this.doDelete(tree.children[i], val);
				}
			}
			else
			{
				System.out.println("6. else");
				if (tree.isLeaf)
				{
					System.out.println("7. tree.isLeaf: " + tree.isLeaf);
					for (int j = i; j < tree.numKeys - 1; j++)
					{
						tree.keys[j] = tree.keys[j+1];
					}
					tree.numKeys--;
					System.out.println("8. tree: " + tree);
					this.repairAfterDelete(tree);
				}
				else
				{
					BTreeNode maxNode = tree.children[i];
					while (!maxNode.isLeaf)
					{
						maxNode = maxNode.children[maxNode.numKeys];
					}
					System.out.println("9. maxNode: " + maxNode);
					tree.keys[i] = maxNode.keys[maxNode.numKeys - 1];
					maxNode.numKeys--;
					System.out.println("10. tree: " + tree + ", maxNode: " + maxNode);
					this.repairAfterDelete(maxNode);					
				}
			}
			System.out.println("do delete final current:\n" + this);
			System.out.println("============ end do delete: " + count + " =========");
		}
	}
	
	AtomicInteger repairAfterDeleteCount = new AtomicInteger();
	private void repairAfterDelete(BTreeNode tree) {
		int count = repairAfterDeleteCount.incrementAndGet();
		System.out.println("========== start repairAfterDelete " + count + " ==============");
		System.out.println("tree: " + tree + ", current:\n" + this);
		if (tree.numKeys < this.min_keys)
		{
			System.out.println("1. tree.numKeys < this.min_keys: " + tree.numKeys + " < " + this.min_keys);
			if (tree.parent == null)
			{
				System.out.println("2. tree.parent == null");
				if (tree.numKeys == 0)
				{
					System.out.println("3. tree.numKeys == 0");
					this.treeRoot = tree.children[0];
					if (this.treeRoot != null)
						this.treeRoot.parent = null;
					System.out.println("4. this.treeRoot = " + this.treeRoot);
					this.resizeTree();
				}
			}
			else
			{
				System.out.println("5. else tree.parent != null");
				BTreeNode parentNode = tree.parent;
				int parentIndex = 0;
				for (; parentNode.children[parentIndex] != tree; parentIndex++);
				System.out.println("6. parentIndex: " + parentIndex + ", parentNode: " + parentNode);
				if (parentIndex > 0 && parentNode.children[parentIndex - 1].numKeys > this.min_keys)
				{
					System.out.println("7. ");
					this.stealFromLeft(tree, parentIndex);
					
				}
				else if (parentIndex < parentNode.numKeys && parentNode.children[parentIndex + 1].numKeys > this.min_keys)
				{
					System.out.println("8. ");
					this.stealFromRight(tree,parentIndex);
					
				}
				else if (parentIndex == 0)
				{
					// Merge with right sibling
					System.out.println("9. ");
					BTreeNode nextNode = this.mergeRight(tree);
					System.out.println("9.1 nextNode: " + nextNode);
					this.repairAfterDelete(nextNode.parent);			
				}
				else
				{
					// Merge with left sibling
					System.out.println("10. ");
					BTreeNode nextNode = this.mergeRight(parentNode.children[parentIndex-1]);
					System.out.println("10.1 nextNode: " + nextNode);
					this.repairAfterDelete(nextNode.parent);			
					
				}
				
			}
			System.out.println("repairAfterDelete final current:\n" + this);
			System.out.println("========== end repairAfterDelete " + count + " ==============");
		}
	}
	
	AtomicInteger stealFromLeftCount = new AtomicInteger();
	private BTreeNode stealFromLeft(BTreeNode tree, int parentIndex) {
		int count = stealFromLeftCount.incrementAndGet();
		System.out.println("========== start stealFromLeft " + count + " ==============");
		BTreeNode parentNode = tree.parent;
		System.out.println("tree: " + tree + ", parentIndex: " + parentIndex + ", current:\n" + this);
		// Steal from left sibling
		tree.numKeys++;
		
		for (int i = tree.numKeys - 1; i > 0; i--)
		{
			tree.keys[i] = tree.keys[i-1];
		}
		BTreeNode leftSib = parentNode.children[parentIndex -1];
		
		System.out.println("1. tree: " + tree + ", parentNode: " + parentNode + ", leftSib: " + leftSib);
		
		if (!tree.isLeaf)
		{
			for (int i = tree.numKeys; i > 0; i--)
			{
				tree.children[i] =tree.children[i-1];
			}
			System.out.println("2. tree: " + tree);
			tree.children[0] = leftSib.children[leftSib.numKeys];
			leftSib.children[leftSib.numKeys] = null;
			tree.children[0].parent = tree;
			System.out.println("3. tree: " + tree + ", leftSib: " + leftSib);
			
		}
		
		tree.keys[0] = parentNode.keys[parentIndex - 1];
		parentNode.keys[parentIndex-1] = leftSib.keys[leftSib.numKeys - 1];
		
		leftSib.numKeys--;
		System.out.println("stealFromLeft final current:\n" + this);
		System.out.println("========== end stealFromLeft " + count + " ==============");
		this.resizeTree();
		return tree;
	}
	
	AtomicInteger stealFromRightCount = new AtomicInteger();
	private BTreeNode stealFromRight(BTreeNode tree, int parentIndex) {
		// Steal from right sibling
		int count = stealFromRightCount.incrementAndGet();
		System.out.println("========== start stealFromRight " + count + " ==============");
		BTreeNode parentNode = tree.parent;
		
		BTreeNode rightSib = parentNode.children[parentIndex + 1];
		
		System.out.println("2. tree: " + tree + ", parentNode: " + parentNode + ", parentIndex: " + parentIndex + ", rightSib: " + rightSib);
		
		tree.numKeys++;
		tree.keys[tree.numKeys - 1] = parentNode.keys[parentIndex];
		parentNode.keys[parentIndex] = rightSib.keys[0];
		System.out.println("3. tree: " + tree + ", parentNode: " + parentNode);
		if (!tree.isLeaf)
		{
			tree.children[tree.numKeys] = rightSib.children[0];
			tree.children[tree.numKeys].parent = tree;
			for (int i = 1; i < rightSib.numKeys + 1; i++)
			{
				rightSib.children[i-1] = rightSib.children[i];
			}
			System.out.println("4. tree: " + tree + ", rightSib: " + rightSib);
			
		}
		for (int i = 1; i < rightSib.numKeys; i++)
		{
			rightSib.keys[i-1] = rightSib.keys[i];
		}
		rightSib.numKeys--;
		System.out.println("5. tree: " + tree + ", rightSib: " + rightSib);
		System.out.println("stealFromRight final current:\n" + this);
		System.out.println("========== end stealFromRight " + count + " ==============");
		this.resizeTree();
		return tree;
	}
	
	AtomicInteger mergeRightCount = new AtomicInteger(); 
	private BTreeNode mergeRight(BTreeNode tree) {
		int count = mergeRightCount.incrementAndGet();
		System.out.println("================== start mergeRight " + count + " =============");
		System.out.println("tree: " + tree + ", current:\n" + this);
		BTreeNode parentNode = tree.parent;
		int parentIndex = 0;
		for (parentIndex = 0; parentNode.children[parentIndex] != tree; parentIndex++);
		System.out.println("tree: " + tree + ", parentNode: " + parentNode + ", parentIndex: " + parentIndex);
		BTreeNode rightSib = parentNode.children[parentIndex+1];
		
		tree.keys[tree.numKeys] = parentNode.keys[parentIndex];
		
		for (int i = 0; i < rightSib.numKeys; i++)
		{
			tree.keys[tree.numKeys + 1 + i] = rightSib.keys[i];
		}
		System.out.println("1. tree: " + tree + ", rightSib: " + rightSib + ", parentNode: " + parentNode + ", current:\n" + this);
		if (!tree.isLeaf)
		{
			System.out.println("2. !tree.isLeaf");
			for (int i = 0; i <= rightSib.numKeys; i++)
			{
				tree.children[tree.numKeys + 1 + i] = rightSib.children[i];
				tree.children[tree.numKeys + 1 + i].parent = tree;
			}
			System.out.println("3. tree: " + tree + ", current:\n" + this);
		}
		for (int i = parentIndex+1; i < parentNode.numKeys; i++)
		{
			parentNode.children[i] = parentNode.children[i+1];
			parentNode.keys[i-1] = parentNode.keys[i];
		}
		System.out.println("4. tree: " + tree + ", parentNode: " + parentNode + ", current:\n" + this);
		parentNode.children[parentNode.numKeys] = null;
		parentNode.numKeys--;
		tree.numKeys = tree.numKeys + rightSib.numKeys + 1;
		System.out.println("5. tree: " + tree + ", parentNode: " + parentNode);
		System.out.println("mergeRight final current:\n" + this);
		System.out.println("================== end mergeRight " + count + " =============");
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
		final int id;
		final static AtomicInteger ID_GENTOR = new AtomicInteger();
		
		BTreeNode(int degree) {
			this.keys = new Integer[degree];
			this.children = new BTreeNode[degree + 1];
			this.numKeys = 1;
			this.isLeaf = true;
			this.parent = null;
			this.id = ID_GENTOR.incrementAndGet();
		}
		
		void toString(List<List<BTreeNode>> all, int index) {
			List<BTreeNode> list = new ArrayList<>();
			for(int i = 0 ; i < children.length ; ++i) {
				if(children[i] == null)
					break;
				if(i > numKeys)
					break;
				list.add(children[i]);
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
			if(numKeys <= 0) {
				builder.append("(" + id + ")");
			}
			else {
				for(int i = 0 ; i < numKeys ; ++i) {
					builder.append(keys[i] + "(" + id + ")");
					if(i < numKeys - 1)
						builder.append(",");
				}
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
		int tierIndex = 0;
		int lastTierIndex = all.size() - 1;
		StringBuilder builder = new StringBuilder();
		for(List<BTreeNode> list : all) {
			int offset = lastTierIndex - tierIndex;
			append(builder, "    ", offset);
			BTreeNode parent = null;
			for(BTreeNode node : list) {
				if(parent == null) {
					builder.append(node);
				}
				else if(parent == node.parent) {
					builder.append(" ").append(node);
				}
				else if(parent != node.parent) {
					builder.append(" - ").append(node);
				}
				parent = node.parent;
			}
			++ tierIndex;
			builder.append("\n");
		}
		return builder.toString();
	}
	
	private void append(StringBuilder builder, String str, int count) {
		for(int i = 0 ; i < count ; ++i)
			builder.append(str);
	}
	
//	public static void main(String[] args) {
//		BTreeDebug tree = new BTreeDebug(5);
//		tree.insert(1);
//		tree.insert(2);
//		tree.insert(3);
//		tree.insert(4);
//		tree.insert(5);
//		tree.insert(6);
//		tree.insert(7);
//		tree.insert(8);
//		tree.insert(9);
//		tree.insert(10);
//		tree.insert(11);
//		tree.insert(12);
//		tree.insert(13);
//		tree.insert(14);
//		tree.insert(15);
//		tree.insert(16);
//		tree.insert(17);
//		tree.insert(18);
//		tree.insert(19);
//		tree.insert(20);
//		tree.insert(21);
//		tree.insert(22);
//		tree.insert(23);
//		tree.insert(24);
//		tree.insert(25);
//		tree.insert(26);
//		tree.insert(27);
//		tree.insert(28);
//		tree.insert(29);
//		tree.insert(30);
//		tree.insert(31);
//		tree.insert(32);
//		tree.insert(33);
//		tree.insert(34);
//		tree.insert(35);
//		tree.insert(36);
//		tree.insert(37);
//		System.out.println(tree);
////		System.out.println(tree.find(7) == null ? "not found" : "found");
////		System.out.println("printTree: " + tree.printTree());
//		tree.delete(21);
//		tree.delete(24);
//		tree.delete(30);
//		tree.delete(34);
//		System.out.println(tree);
////		System.out.println(tree.find(7) == null ? "not found" : "found");
//	}
	
	public static void main(String[] args) {
		BTreeDebug tree = new BTreeDebug(7);
		for(int i = 1 ; i <= 100 ; ++i)
			tree.insert(i);
		System.out.println(tree);
//		System.out.println(tree.find(7) == null ? "not found" : "found");
//		System.out.println("printTree: " + tree.printTree());
		tree.delete(20);
		tree.delete(21);
		tree.delete(22);
//		System.out.println(tree);
//		System.out.println(tree.find(7) == null ? "not found" : "found");
	}
	
//	public static void main(String[] args) {
//		BTreeDebug tree = new BTreeDebug(3);
//		tree.insert(1);
//		tree.insert(2);
//		tree.insert(3);
//		tree.insert(4);
//		tree.insert(5);
//		tree.insert(6);
//		tree.insert(7);
//		tree.insert(8);
//		tree.insert(9);
//		tree.insert(10);
//		tree.insert(11);
//		tree.insert(12);
//		tree.insert(13);
//		tree.insert(14);
//		tree.insert(15);
//		tree.insert(16);
//		tree.insert(17);
//		System.out.println(tree);
////		System.out.println(tree.find(7) == null ? "not found" : "found");
////		System.out.println("printTree: " + tree.printTree());
//		tree.delete(8);
//		System.out.println(tree);
////		System.out.println(tree.find(7) == null ? "not found" : "found");
//	}
}
