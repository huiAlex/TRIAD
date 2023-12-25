package edu.nd.dronology.monitoring.tree;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import edu.nd.dronology.core.monitoring.ArtifactIdentifier;
import edu.nd.dronology.util.Immutables;
import edu.nd.dronology.util.NullUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * A tree node which can hold elements at a node (a set of ArtifactIdentifier)
 * and<br/>
 * have some children identified by ArtifactIdentifier.
 * 
 * 
 */

public class TreeNode {
	private static final ILogger LOGGER = LoggerProvider.getLogger(TreeNode.class);

	private final ArtifactIdentifier artifactIdentifier;

	private final Set<ArtifactIdentifier> elements = new HashSet<>();

	private final WeakHashMap<ArtifactIdentifier, TreeNode> children = new WeakHashMap<>();

	private TreeNode parent;

	public TreeNode(ArtifactIdentifier id) {
		NullUtil.checkNull(id);
		this.artifactIdentifier = id;
	}

	public synchronized ArtifactIdentifier getArtifactIdentifier() {
		return artifactIdentifier;
	}

	/**
	 * Returns an existing child or creates a new one, if none present.
	 * 
	 * @param id
	 * @return The child with the given identifier.
	 */
	public synchronized TreeNode getChild(final ArtifactIdentifier id) {
		if (id == null) {
			throw new IllegalArgumentException("Parameter id can't be null!");
		}
		if (children.containsKey(id)) {
			return children.get(id);
		}
		TreeNode node = new TreeNode(id);
		children.put(id, node);
		node.setParent(this);
		return node;
	}

	private void setParent(TreeNode treeNode) {
		this.parent = treeNode;

	}

	public synchronized boolean addElement(ArtifactIdentifier id) {
		if (elements.contains(id)) {
			for (ArtifactIdentifier elem : elements) {
				if (elem.equals(id)) {
					elem.attachItems(id.getAttachedItems());
					return true;
				}
			}
		}
		return elements.add(id);
	}

	public synchronized Set<ArtifactIdentifier> getElements() {
		return Immutables.hashSetCopy(elements);
	}

	public synchronized Map<ArtifactIdentifier, TreeNode> getChildren() {
		return Immutables.hashMapCopy(children);
	}

	/**
	 * Remove all children and elements.
	 */
	public synchronized void clear() {
		children.clear();
		elements.clear();
	}

	@Override
	public synchronized int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((artifactIdentifier == null) ? 0 : artifactIdentifier.hashCode());
		return result;
	}

	@Override
	public synchronized boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TreeNode other = (TreeNode) obj;
		if (artifactIdentifier == null) {
			if (other.artifactIdentifier != null) {
				return false;
			}
		} else if (!artifactIdentifier.equals(other.artifactIdentifier)) {
			return false;
		}
		return true;
	}

	@Override
	public synchronized String toString() {
		return "TreeNode [artifactIdentifier=" + artifactIdentifier + ", children=" + children + ", elems=" + elements
				+ "]";
	}

	/**
	 * Debug-only diagnostic output.
	 */
	public synchronized void toLog() {
		LOGGER.debug("artifactIdentifier=" + artifactIdentifier);
		LOGGER.debug("elems=" + elements);
		for (Map.Entry<ArtifactIdentifier, TreeNode> childEntries : children.entrySet()) {
			LOGGER.debug("---");
			LOGGER.debug("child '" + childEntries.getKey() + "':");
			LOGGER.debug(childEntries.getValue());
		}

	}

	public TreeNode getParent() {
		return parent;
	}

	public void remove(TreeNode node) {
		children.remove(node.getArtifactIdentifier());
	}
}
