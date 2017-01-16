DT.java

The main program DT calls program ID3, which is the source code for the ID3 algorithm. The ID3 class has methods that build a tree and read it, and then DT uses the tree built to predict labels and output the confusion matrix. They both rely on the TreeNode class, which holds the data of the node and a HashMap of children.

To use the DT program, run it and then follow the prompts. The confusion matrix will be displayed on std.out.

The files monks1.csv and opticalDigit.csv are provided for testing, but other csv files may be used (label must be in the first column)
