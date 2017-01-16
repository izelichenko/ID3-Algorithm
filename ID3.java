import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Isabella Zelichenko
 */
public class ID3 {
    /**
     * Initializes an ID3 object
     */
    public ID3 (){

    }

    /**
     * Builds and returns the tree based on ID3 algorithm.
     *
     * @param att
     * @param set
     * @param values
     * @return The tree that you build
     *
     */
    public TreeNode<String> buildTree (ArrayList<String> att,
            ArrayList<HashMap<String, String>> set, HashMap<String, ArrayList<String>> values){
        TreeNode<String> N = null; //create new tree node, set to null for now.
        if (att.isEmpty() || allSame(set)){ //if attribute list is empty, or all labels same
            N = new TreeNode<String>(mostCommon(set)); //the node is a leaf with data of most common label
            return N;
        }
        else {
            String aBest = null; //initialize String a* for best attribute
            double max = -1;
            for (int i = 0; i < att.size(); i++){ //calculate Gain for each attribute
                double curGain = gain(set, att.get(i));
                if (curGain > max){
                    max = curGain;
                    aBest = att.get(i); //set a* to be attribute with highest Gain
                }
            }
            N = new TreeNode<String>(aBest); //the node is not a leaf and has data a*
            HashMap<String, ArrayList<HashMap<String, String>>> subsets = getSubsets(set, aBest); //subsets for all available values for a*
           
            for (int i = 0; i < values.get(aBest).size(); i++){
                String next = values.get(aBest).get(i); //next value;
                if (subsets.containsKey(next)==false){ //if this value has already been covered (so there wouldn't be a subset for it in 'subsets')
                    N.addChild(next, new TreeNode<String>(mostCommon(set))); //the node is a leaf with data of most common label
                }
                else {
                    ArrayList<String> attCopy = new ArrayList<String>(att); //copy of att
                    attCopy.remove(aBest); //delete aBest from copy instead of original att
                    N.addChild(next, buildTree(attCopy, subsets.get(next), values)); //call buildTree on att list without aBest and subset of current value
                }
            }

            return N;
        }

    }
    
    /**
     * Reads built tree to find label for given instance.
     *
     * @param inst
     * @param tree
     * @return The data in the leaf that you reach
     *
     */
    public String readTree (HashMap<String, String> inst, TreeNode<String> tree){
        if (tree.isLeaf()){
            return tree.getData();
        }

        else {
            return readTree(inst, tree.getChild(inst.get(tree.getData()))); //recurse on same instance and child tree based on value of current attribute
            //current attribute = data of current Node.
        }
    }
   
   /**
     * Checks if the lables in the given set are all the same
     *
     * @param set
     * @return True if the labels are the same, false otherwise
     *
     */
    private boolean allSame(ArrayList<HashMap<String, String>> set){
        HashMap<String, Integer> labels = countAtts(set, "label"); //build hashmap with label counts
        return (labels.size()==1); //if all labels the same, the map should have 1 entry.
    }
    
    /**
     * Finds the most common label in the given set.
     *
     * @param set
     * @return The most common attribute as a String
     *
     */
    private String mostCommon (ArrayList<HashMap<String, String>> set){
        HashMap<String, Integer> labels = countAtts(set, "label"); //build hashmap with label counts
        Set<String> labelNames = labels.keySet();
        Iterator<String> it = labelNames.iterator();

        String common = it.next(); //first label
        int max = labels.get(common); //set value of first label as max
        while (it.hasNext()){
            String next = it.next();
            if (labels.get(next)>max){
                max = labels.get(next);
                common = next;
            }
        }

        return common;
    }
    
    /**
     * Calculates the entropy of the given instance.
     *
     * @param inst
     * @return The value of the entropy as a double
     *
     */
    private double entropy (ArrayList<HashMap<String, String>> inst){
        int len = inst.size();
        double sum = 0.0;
        HashMap<String, Integer> labels = countAtts(inst, "label"); //build hashmap with label counts

        Collection<Integer> values = labels.values();
        Iterator<Integer> it = values.iterator(); //iterate over the counts
        while (it.hasNext()){
            double pNext = (double) it.next() / (double) len;
            sum += pNext * (Math.log(pNext)/Math.log(2));
        }
        return sum*-1; //negate the sum
    }
    
    /**
     * Calculates the Gain of the given attribute within the given set.
     *
     * @param set
     * @param att
     * @return The value of the Gain as a double
     *
     */
    private double gain (ArrayList<HashMap<String, String>> set, String att){
        double sum = 0.0;
        HashMap<String, ArrayList<HashMap<String, String>>> subsets //keep subsets for each value; also used to count appearances of values
                = getSubsets(set, att);

        Collection<ArrayList<HashMap<String, String>>> vals = subsets.values();
        Iterator<ArrayList<HashMap<String, String>>> it = vals.iterator();
        while (it.hasNext()){ //|S_v| / |S| * H(S_v) --> summation
            ArrayList<HashMap<String, String>> curVal = it.next();
            double prop = (double) curVal.size() / (double) set.size(); //proportion of #value to total
            sum += prop * (entropy(curVal));
        }

        return entropy(set) - sum; //subtract sum from entropy of the whole set
    }
    
    /**
     * Returns a hashmap of values of the given attribute mapped to the set of all instances with that value.
     *
     * @param att
     * @param set
     * @return Hashmap of subsets
     *
     */
    private HashMap<String, ArrayList<HashMap<String, String>>> getSubsets (ArrayList<HashMap<String, String>> set,
            String att){
        HashMap<String, ArrayList<HashMap<String, String>>> subsets //keep subsets for each value; also used to count appearances of values
                = new HashMap<String, ArrayList<HashMap<String, String>>>();
        for (int i = 0; i < set.size(); i++){ //counting values
           String val = set.get(i).get(att);
            if (subsets.containsKey(val)){
                subsets.get(val).add(set.get(i)); //add this instance to arraylist corresponding to current value
            } else { //first appearance of this value
                subsets.put(val, new ArrayList<HashMap<String, String>>()); //initiate AL for corresponding value
                subsets.get(val).add(set.get(i)); //add to subset of corresponding value
            }
        }
        return subsets;
    }
    
    /**
     * Returns a hashmap of values of the given attribute mapped to the number of times that value appears in the set.
     *
     * @param name
     * @param set
     * @return Hashmap of values to integers
     *
     */
    private HashMap<String, Integer> countAtts (ArrayList<HashMap<String, String>> set, String name){
        HashMap<String, Integer> labels = new HashMap<String, Integer>();
        for (int i = 0; i < set.size(); i++){
            String l = set.get(i).get(name); //label of first instance
            if (labels.containsKey(l)){
                labels.put(l, labels.get(l)+1);
            } else {
                labels.put(l, 1);
            }
        }

        return labels;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    }   
}
