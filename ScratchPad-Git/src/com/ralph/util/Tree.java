package com.ralph.util;

import java.util.ArrayList;
import java.util.List;

public class Tree {
	private Node root;

	public Tree(String rootData) {
		root = new Node();
		root.data = rootData;
		root.children = new ArrayList<Node>();
	}

	public class Node {
		private String data;
		private Node parent;
		private List<Node> children;

		public Node() {

		}
		public Node(String data) {
			this.data = data;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public Node getParent() {
			return parent;
		}
		public void setParent(Node parent) {
			this.parent = parent;
		}
		public List<Node> getChildren() {
			return children;
		}
		public void setChildren(List<Node> children) {
			this.children = children;
		}
	}


}
