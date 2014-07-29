package structures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	//DONE
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		root = buildHelper();
	}	
	private TagNode buildHelper(){
		if(sc.hasNextLine()){    
			String tmp = sc.nextLine();
			if(tmp.length() > 1 && tmp.charAt(1) == '/'){
				return null;
			}else if(tmp.charAt(0) == '<'){
				tmp = tmp.substring(1,tmp.length()-1);
				TagNode n = new TagNode(tmp,null,null);
				n.firstChild = buildHelper();
				n.sibling = buildHelper();
				return n;
			}else{
				TagNode n = new TagNode(tmp, null, null);
				n.sibling = buildHelper();
				return n;
			}
		}else{
			return null;
		}
	}
		
	//DONE
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		if(oldTag.equals(newTag)){
			return;
		}
		replaceTag(oldTag, newTag, root);
	}
	private void replaceTag(String oldTag, String newTag, TagNode root){
		if(root == null){
			return;
		}
		replaceTag(oldTag, newTag, root.firstChild);
		if((oldTag.equals("em") && newTag.equals("b")) || (oldTag.equals("b") && newTag.equals("em")) || (oldTag.equals("ol") && newTag.equals("ul")) || (oldTag.equals("ul") && newTag.equals("ol"))){
			if(root.tag.equals(oldTag)){
				root.tag = newTag;
			}
		}
		replaceTag(oldTag, newTag, root.sibling);
	}
	
	//DONE
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		TagNode ptr = findTable(root); //find the table's location to start from there
		if(ptr != null){ //if there is a table
			int count = 1;
			if(ptr.firstChild.tag.equals("tr")){
				ptr = ptr.firstChild; //start at the first row
				while(count != row && ptr != null){
					ptr = ptr.sibling; //keep moving until you've hit the right row
					count++;
				}
				if(count != row){ //if int row is more than the actual amount of rows, return
					return;
				}
			}
			if(ptr.firstChild.tag.equals("td")){ 
				ptr = ptr.firstChild; //start at the first child of the correct row
				while(ptr != null){ 
					TagNode temp = ptr.firstChild; //set the child (the column's item) to a temp
					ptr.firstChild = new TagNode("b", temp,null);  //create the bold tag, make it's child the temp
					ptr = ptr.sibling; //move to the next td
				}
			}
		}
	}
	private TagNode findTable(TagNode root){
		TagNode table = null;
		if(root == null){
			return null;
		}
		table = findTable(root.firstChild);
		if(table != null){
			return table;
		}
		if(root.tag.equals("table")){
			table = root;
			return table;
		}
		table = findTable(root.sibling);
		return table;
	}
	
	//DONE
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		removeTag(tag, root);
	}	
	private TagNode removeTag(String tag, TagNode root){
		if(root == null){
			return null;
		}
		TagNode child = removeTag(tag, root.firstChild);
		
		TagNode sibling = removeTag(tag, root.sibling);
		
		if(tag.equals(root.tag)){
			if(tag.equals("ol") || tag.equals("ul")){
				TagNode curr = root.firstChild;
				for(curr = root.firstChild; curr != null; curr = curr.sibling){
					if(curr.tag.equals("li")){
						curr.tag = "p";
					}
				}
			}
			TagNode ptr = child;
			TagNode prev = null;
			for(ptr = child; ptr != null; ptr = ptr.sibling){
				prev = ptr;
			}
			prev.sibling = sibling;
			return child;
		}else{
			root.sibling = sibling;
			root.firstChild = child;
			return root;
		}
	}
	
	//Extra spaces??
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag){
		/** COMPLETE THIS METHOD **/
		if(!(tag.equals("em") || tag.equals("b"))){
			return;
		}
		addTag(word,tag,root);
	}
	private void addTag(String word, String tag, TagNode root){
		if(root == null){
			return;
		}
		TagNode portera = root;
		TagNode prev = null;
		for(portera = root; portera != null; portera = portera.firstChild){ //traverse the tree, get to the leaves, where the text is
			prev = portera; //we need the prev, because after this loop, ptr will be null
			addTag(word,tag, prev.sibling); //recursively do this method on all siblings
		}
		if(!isTag(prev.tag)){ //if there is text
			String currTag = prev.tag;
			String wordForNode = "";
			for(int i = 0; i < currTag.length(); i++){ //go through the text
				if(currTag.charAt(i) == word.charAt(0) && (i-1 >= 0 && currTag.charAt(i-1) == ' ')){ //looking for the word
					int j = 0;
					for(j = i; j < currTag.length(); j++){ //when you find it, get it
						if(currTag.charAt(j) != ' '){ //by going through from the first letter until a space
							wordForNode += currTag.charAt(j);
						}else{
							break;
						}
					}
					if(wordForNode.equalsIgnoreCase(word) || wordForNode.equalsIgnoreCase(word + '.') || wordForNode.equalsIgnoreCase(word + ',') || wordForNode.equalsIgnoreCase(word + '?') || wordForNode.equalsIgnoreCase(word + '!') || wordForNode.equalsIgnoreCase(word + ':') || wordForNode.equalsIgnoreCase(word + ';')){ //if it's the word, with the appropriate punctuations allowed
						prev.tag = currTag.substring(0,i); //change the prev, make it a substring of everything before the word
						String editedTag = currTag.substring(i,j); //get the word separately
						String nextTag = currTag.substring(j); //get everything after the word separately
						TagNode nTag = new TagNode(nextTag, null, prev.sibling);
						TagNode eTag = new TagNode(editedTag, null, null); //make a new node for "tag"'s tagNode's child
						TagNode addedTag = new TagNode(tag, eTag, nTag); //make a new node for "tag"'s tagNode, set it's child and siblings
						prev.sibling = addedTag; //set prev's sibling
					}
				}
				addTag(word, tag, prev.sibling);
			}
		}
		nullDestroyer();
	}
	private void nullDestroyer(){
		nullDestroyer(root);
	}	
	private TagNode nullDestroyer(TagNode root){
		if(root == null){
			return null;
		}
		TagNode child = nullDestroyer(root.firstChild);
			
		TagNode sibling = nullDestroyer(root.sibling);
			
		if(root.tag.equals("")){
			return child;
		}else{
			root.sibling = sibling;
			root.firstChild = child;
			return root;
		}
	}
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	private boolean isTag(String tag){
		if(tag.equals("b") || tag.equals("em") || tag.equals("html") || tag.equals("body") || tag.equals("table") || tag.equals("tr") || tag.equals("td") || tag.equals("ol") || tag.equals("ul") || tag.equals("li") || tag.equals("p")){
			return true;
		}else{
			return false;
		}
	}
}
