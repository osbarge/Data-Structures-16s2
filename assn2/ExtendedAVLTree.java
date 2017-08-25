package net.datastructures;


//------------------

//--
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
//------------------

public class ExtendedAVLTree<K,V> extends AVLTree<K,V> {
	
	public static List LofK = new ArrayList();
    public static List LofV = new ArrayList();
    public static List LofH = new ArrayList();

	
	
	/* Question1:
	 * Public static <K, V>  AVLTree<K, V> clone(AVLTree<K,V> tree)
	 * This  class  method  creates  an  identical  copy  of  the  AVL  tree   
	 * specified  by  the parameter and returns a reference to the new AVL tree
	 */
	public static <K, V> AVLTree<K, V> clone(AVLTree<K, V> tree) {

        AVLTree<K, V> clonedTree = new AVLTree<K, V>();
        clonedTree.addRoot(tree.root().element());
        recursiveCloner(tree.root(), tree, clonedTree.root(), clonedTree);
        clonedTree.size = tree.size;
		clonedTree.numEntries = tree.numEntries;
        return clonedTree;
        
    }
	
    /*
     * function name: recursiveCloner ; 
     * parameters: Position<Entry<K, V>> v, AVLTree<K,V> original, Position<Entry<K, V>> v2, AVLTree<K, V> NewTree return: AVLTree
     * description: copy node v in original and create a new node in the same position in NewTree
     */
    private static <K, V> void recursiveCloner(Position<Entry<K, V>> v, AVLTree<K, V> original, Position<Entry<K, V>> v2, AVLTree<K, V> NewTree) {

        if (original.hasLeft(v)) {
            NewTree.insertLeft(v2, original.left(v).element());
            recursiveCloner(original.left(v), original, NewTree.left(v2), NewTree);
        }
        if (original.hasRight(v)) {
            NewTree.insertRight(v2, original.right(v).element());
            recursiveCloner(original.right(v), original, NewTree.right(v2), NewTree);
        }
        if(v2.element()!=null) {
        	//assign the height to current parent node
			NewTree.setHeight(v2);
		}
    }
    
    
    
    /*****************************OSCAR*****************************************/
      	
  	static <K, V> void print(AVLTree<K, V> tree) {
        LofK.clear();
        LofV.clear();
        LofH.clear();
        Position<Entry<K, V>> RootP = tree.root();
        int Height = 0;
        treeToLists(RootP, tree, Height);

//        System.out.println(LofK);
//        System.out.println(LofV);
//        System.out.println(LofH);
        new Drawtree(LofK, LofV, LofH);

  	}
  	
  	/*
     * function name: treeToLists 
     * parameters: Position<Entry<K, V>> v, AVLTree<K, V> tree, int Height
     * return: void
     * description: search the AVLTree and store keys in a List , structure conversion from tree to lists
     */

    public static <K, V> void treeToLists(Position<Entry<K, V>> v, AVLTree<K, V> tree, int Height) {
        Height++;
        if (tree.hasLeft(v)) {
            treeToLists(tree.left(v), tree, Height);
        }
        if (tree.isInternal(v)) {
            LofK.add(v.element().getKey());
            LofV.add(v.element().getValue());
            LofH.add(Height);
            
            if (Height == tree.height(v)) {
            	System.out.println("para el nodo " + v.element().getKey() + " son iguales" + Height + " y " + tree.height(v) );
            } else {
            	System.out.println("para el nodo " + v.element().getKey() + " NO son iguales"  + Height + " y " + tree.height(v) );
            }
        }
        if (tree.hasRight(v)) {
            treeToLists(tree.right(v), tree, Height);
        }
    }
  	
    /***************************** </ OSCAR >**************************************/
    
    
    
    
    
    /*
     * MAIN METHOD
     */
    public static void main(String[] args)
    { 
      String values1[]={"Sydney", "Beijing","Shanghai", "New York", "Tokyo", "Berlin",
     "Athens", "Paris", "London", "Cairo"}; 
      int keys1[]={20, 8, 5, 30, 22, 40, 12, 10, 3, 5};
      String values2[]={"Fox", "Lion", "Dog", "Sheep", "Rabbit", "Fish"}; 
      int keys2[]={40, 7, 5, 32, 20, 30};
         
      /* Create the first AVL tree with an external node as the root and the
     default comparator */ 
         
        AVLTree<Integer, String> tree1=new AVLTree<Integer, String>();

      // Insert 10 nodes into the first tree
         
        for ( int i=0; i<10; i++)
            tree1.insert(keys1[i], values1[i]);
       
      /* Create the second AVL tree with an external node as the root and the
     default comparator */
         
        AVLTree<Integer, String> tree2=new AVLTree<Integer, String>();
       
      // Insert 6 nodes into the tree
         
        for ( int i=0; i<6; i++)
            tree2.insert(keys2[i], values2[i]);
         
        ExtendedAVLTree.print(tree1);
        //ExtendedAVLTree.print(ExtendedAVLTree.clone(tree2));
        
        /* Codigo original, descomentar desps
        ExtendedAVLTree.print(tree1);
        ExtendedAVLTree.print(tree2); 
        ExtendedAVLTree.print(ExtendedAVLTree.clone(tree1));
        ExtendedAVLTree.print(ExtendedAVLTree.clone(tree2));
        ExtendedAVLTree.print(ExtendedAVLTree.merge(ExtendedAVLTree.clone(tree1), ExtendedAVLTree.clone(tree2)));*/
        
      }




}


class Drawtree extends Frame {

    public List Keys = new ArrayList();
    public List Values = new ArrayList();
    public List Height = new ArrayList();
    int Heightest = 0;

    public void setKeys(List LofK) {
        for (int i = 0; i < LofK.size(); i++) {
            this.Keys.add(LofK.get(i));
        }
    }

    public void setValues(List LofV) {
        for (int i = 0; i < LofV.size(); i++) {
            this.Values.add(LofV.get(i));
        }
    }

    public void setHeight(List LofH) {
        for (int i = 0; i < LofH.size(); i++) {
            this.Height.add(LofH.get(i));
            if ((int) this.Height.get(i) > Heightest) {
                Heightest = (int) this.Height.get(i);
            }
        }
    }

    public Drawtree(List LofK, List LofV, List LofH) {
        //super(); 
        setKeys(LofK);
        setValues(LofV);
        setHeight(LofH);
        setBounds(0, 0, 100, 100);

        setBackground(Color.white);
        setSize(1000, 700);
        setVisible(true);
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                Drawtree.this.dispose();
            }
        });

    }
// rewrite function
    public void paint(Graphics g) {
        g.setColor(Color.black);
        //x of key is x of cycle + 1; y of key is y of cycle + 13; so that number is suitable for the cycle
        // draw internal nodes
        for (int i = 0; i < Keys.size(); i++) {
            int x = 100;
            int y = 500;
            x = x + 60 * i;
            y = y + 80 * ((int) Height.get(i) - Heightest);
            
            System.out.println("oz. key: "+ Keys.get(i) + "altura: "+ Height.get(i) + "+"+Heightest);
            
            g.drawOval(x, y, 16, 16);
            String data1 = Keys.get(i).toString();
            int KV = (int) Keys.get(i);
            if (KV / 10 == 0) {
                g.drawString(data1, x + 5, y + 13);
            } else {
                g.drawString(data1, x + 1, y + 13);
            }
        }
        // draw leaves and branches between nodes and leaves
        for (int i = 1; i <= Heightest; i++) {
            for (int k = 0; k < Keys.size(); k++) {
                if ((int) Height.get(k) == i) {
                    int x = 100 + 60 * k;
                    int y = 500 + 80 * ((int) Height.get(k) - Heightest);
                    if (has_left_child(k) == -1) {

                        int LX = x - 30;
                        int LY = y + 80;
                        g.drawRect(LX, LY, 15, 10);
                        g.drawLine(x + 8, y + 16, LX + 5, LY);
                    }
                    if (has_right_child(k) == -1) {

                        int LX = x + 30;
                        int LY = y + 80;
                        g.drawRect(LX, LY, 15, 10);
                        g.drawLine(x + 8, y + 16, LX + 5, LY);
                    }
                }
            }
        }

        // draw branches between nodes
        for (int i = 1; i <= Heightest; i++) {
            for (int k = 0; k < Keys.size(); k++) {
                if ((int) Height.get(k) == i) {
                    int x = 100 + 60 * k;
                    int y = 500 + 80 * ((int) Height.get(k) - Heightest);
                    if (has_left_child(k) != -1) {
                        int dex = has_left_child(k);
                        int LX = 100 + 60 * dex;
                        int LY = 500 + 80 * ((int) Height.get(dex) - Heightest);
                        g.drawLine(x + 8, y + 16, LX + 8, LY);
                    }
                    if (has_right_child(k) != -1) {
                        int dex = has_right_child(k);
                        int LX = 100 + 60 * dex;
                        int LY = 500 + 80 * ((int) Height.get(dex) - Heightest);
                        g.drawLine(x + 8, y + 16, LX + 8, LY);
                    }
                }
            }
        }
    }

    public int getParent(int index) {
        int no1 = -1;
        int no2 = -1;
        if ((int) Height.get(index) == 1) {
            return index;
        }
        for (int i = index - 1; i >= 0; i--) {
            if ((int) Height.get(index) == (int) Height.get(i) + 1) {
                no1 = i;
                break;
            }
        }
        for (int i = index + 1; i < Keys.size(); i++) {
            if ((int) Height.get(index) == (int) Height.get(i) + 1) {
                no2 = i;
                break;
            }
        }
        if (no1 == -1 && no2 != -1) {
            return no2;
        } else if (no2 == -1 && no1 != -1) {
            return no1;
        } else if (no2 == -1 && no1 == -1) {
            return index;
        } else {
            if (index - no1 < no2 - index) {
                return no1;
            } else {
                return no2;
            }
        }
    }

    public int has_left_child(int index) {
        int no = -1;// no child
        if ((int) Keys.get(index) <= (int) Keys.get(getParent(index))) {
            for (int i = index - 1; i >= 0; i--) {
                if ((int) Height.get(i) - (int) Height.get(index) < 0) {
                    break;
                }
                if ((int) Height.get(index) + 1 == (int) Height.get(i)) {
                    no = i;
                    break;
                }
            }
        } else {
            for (int i = index - 1; i >= getParent(index); i--) {
                if ((int) Height.get(i) - (int) Height.get(index) < 0) {
                    break;
                }
                if ((int) Height.get(index) + 1 == (int) Height.get(i)) {
                    no = i;
                    break;
                }
            }
        }
        return no;
    }

    public int has_right_child(int index) {
        int no = -1;// no child
        if ((int) Keys.get(index) < (int) Keys.get(getParent(index))) {
            for (int i = index + 1; i <= getParent(index); i++) {
                if ((int) Height.get(i) - (int) Height.get(index) < 0) {
                    break;
                }
                if ((int) Height.get(index) + 1 == (int) Height.get(i)) {
                    no = i;
                    break;
                }
            }
        } else {
            for (int i = index + 1; i < Keys.size(); i++) {
                if ((int) Height.get(i) - (int) Height.get(index) < 0) {
                    break;
                }
                if ((int) Height.get(index) + 1 == (int) Height.get(i)) {
                    no = i;
                    break;
                }
            }
        }
        return no;
    }
}
