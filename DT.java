import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Random;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Isabella Zelicheko
 */
public class DT {

    private ArrayList<HashMap<String, String>> instances = new  ArrayList<HashMap<String, String>>();
    private ArrayList<String> atts = new ArrayList<String>();
    private HashMap<String, ArrayList<String>> values = new HashMap<String, ArrayList<String>>();

    private HashMap<String, Integer> labels;
    private Integer[][] conMatrix;
    
    /**
     * Constructor for DT class; deconstructs given file, creates tree for given algorithm, reads tree and outputs confusion matrix.
     *
     * @param filename
     * @param algorithm
     * @param rand
     *
     */
    public DT (String filename, String algorithm, int rand) {
        Scanner file = null;
        try {
            file = new Scanner (new File(filename));
        } catch (FileNotFoundException e) {
            System.out.println("Problem opening file: " + e.getMessage());
            System.exit(1);
        }

        String firstLine = file.nextLine();
        String[] names = firstLine.split(",");

        ////BUILDING LIST OF ATTRIBUTES////
        for (int i = 1; i < names.length; i++){ //ignore i = 0, because that is the label
                //use substring to get rid of quotation marks
                this.atts.add(names[i].substring(1, names[i].length()-1)); //building ArrayList of attributes to pass to algorithm
        }

        while(file.hasNextLine()){ //Reading in instances
            String l = file.nextLine(); //next instance
            String[] values = l.split(","); //split values
            HashMap<String, String> thisInst = new HashMap<String, String>(); //make hashmap for this instance

            for (int i = 0; i < values.length; i++){ //building map for instance (incl. labels)
                thisInst.put(names[i].substring(1, names[i].length()-1), values[i]); //match name of attribute to its value

            }

            this.instances.add(thisInst); //add to array of instances
        }
        
        //Now we have our arraylist of instances
        ////BUILDING HASHMAP OF VALUES FOR EACH ATTRIBUTE TO PASS TO DT ALGORITHMS////
        for (int i = 0; i < atts.size(); i++){ //for each attribute
                ArrayList<String> vals = new ArrayList<String>(); //create arraylist to store its values
                Set<String> keys = countAtts(instances, atts.get(i)).keySet(); //get values through countAtts function
                Iterator<String> it = keys.iterator(); //create iterator for keys
                while (it.hasNext()){
                        vals.add(it.next()); //add each value to array
                }
                values.put(atts.get(i), vals); //add this attribute and its corresponding arraylist to hashmap
        }

        ////BUILDING MATRIX////
        labels = countAtts(instances, "label"); //isolate labels
        conMatrix = new Integer[labels.size()][labels.size()]; //build matrix based on number of labels
        for (int i = 0; i < conMatrix.length; i++){ //initializing each space in matrix
            for (int j = 0; j < conMatrix.length; j++){
                conMatrix[i][j] = 0;
            }
        }
        
        //SHUFFLING ARRAYLIST//
        Random rng = new Random(rand);
        ArrayList<HashMap<String, String>> shuffled = new ArrayList<HashMap<String, String>>(instances);
        Collections.shuffle(shuffled, rng);

        ArrayList<HashMap<String, String>> train = new  ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> test = new  ArrayList<HashMap<String, String>>();
        int trainAmt = (instances.size()*70)/100; //70% of instances will be used for training

        for (int i = 0; i < trainAmt; i++){ //creating training set
            train.add(shuffled.get(i));
        }

        for (int i = trainAmt; i < instances.size(); i++){
            test.add(shuffled.get(i));
        }

        if (algorithm.equals("ID3")){
            ID3 id3 = new ID3();
            TreeNode<String> tree = id3.buildTree(this.atts, train, this.values);

            ArrayList<String> labelVals = new ArrayList<String>();
            labelVals.addAll(labels.keySet()); //all possible labels


            }

            System.out.println("\nConfusion Matrix: ");
            for (int i = 0; i < labelVals.size(); i++){
                System.out.print(labelVals.get(i).substring(1, labelVals.get(i).length()-1) + ", ");
            }
            System.out.println();
            for (int i = 0; i < conMatrix.length; i++){
                for (int j = 0; j < conMatrix.length; j++){
                    System.out.print (conMatrix[i][j] + ", ");
                }
                System.out.println(labelVals.get(i).substring(1, labelVals.get(i).length()-1));
            }
        }
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
            String l = set.get(i).get(name); //label of each instance
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
        Scanner input = new Scanner(System.in);
        System.out.println("Enter file name: ");
        String file = input.next();

        System.out.println("Choose algorithm (ID3): ");
        String alg = input.next();

        System.out.println("Enter random seed: ");
        int rand = input.nextInt();

        DT datatree = new DT(file, alg, rand);
    }
}
