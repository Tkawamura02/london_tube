
public class SinglyLinkedList<E> {
	private static class Node<E> {
		private E element;
		private Node<E> next;

		public Node(E e, Node<E> n) {
			element = e;
			next = n;
		}

		public E getElement() {
			return element;
		}

		public Node<E> getNext() {
			return next;
		}

		public void setNext(Node<E> n) {
			next = n;
		}
	}

	private Node<E> head;
	private Node<E> tail;
	private int size;

	public SinglyLinkedList() {
		head = null;
		tail = null;
		size = 0;
	}

	public int length() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public E getFirst() {
		if (isEmpty()) {
			return null;
		}
		return head.getElement();
	}

	public E getLast() {
		if (isEmpty()) {
			return null;
		}
		return tail.getElement();
	}

	public void addFirst(E e) {
		head = new Node<E>(e, head);
		if (isEmpty()) {
			tail = head;
		}
		size++;
	}

	public void addLast(E e) {
		Node<E> newest = new Node<E>(e, null);
		if (isEmpty()) {
			head = newest;
		} else {
			tail.setNext(newest);
		}
		tail = newest;
		size++;
	}

	public E removeFirst() {
		if (isEmpty()) {
			return null;
		}

		E answer = head.getElement();
		head = head.getNext();
		size--;
		if (isEmpty()) {
			tail = null;
		}
		return answer;
	}
	
	public String toString() {
		String result = length() + ":[";
		for (Node<E> p = head; p != null; p = p.getNext()) {
			result += p.getElement();
			if (p.getNext() != null) {
				result += ", ";
			}
		}
		result += "]";
		return result;
	}
}