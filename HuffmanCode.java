import java.util.*;
import java.util.Scanner.*;
import java.io.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.*;
import java.util.Arrays;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap.*;
public class HuffmanCode {
    static HashMap<String,String> dict1 = new HashMap<String,String>();
	static HashMap<String,Integer> dict2 = new HashMap<String,Integer>();
	static String[] hexchars = {"\n","\r"," ","0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
	static HuffmanTree tree; static HuffmanTree tree1; static HuffmanTree tree2;
	static DataInputStream inp,x; static FileInputStream RIn;
	static DataOutputStream out;
	static BufferedImage grayscaleImage,image;
	static BufferedReader bf;
	static Scanner scanner,sc;
	static int ch=0;static String everything;
		
    // input is an array of frequencies, indexed by character code
    public static HuffmanTree buildTree(int[] charFreqs) {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();
        // initially, we have a forest of leaves
        // one for each non-empty character
        for (int i = 0; i < charFreqs.length; i++)
            if (charFreqs[i] > 0)
                trees.offer(new HuffmanLeaf(charFreqs[i], (char)i));
        assert trees.size() > 0;
        // loop until there is only one tree left
        while (trees.size() > 1) {
            // two trees with least frequency
            HuffmanTree a = trees.poll();
            HuffmanTree b = trees.poll();
 
            // put into new node and re-insert into queue
            trees.offer(new HuffmanNode(a, b));
        }
        return trees.poll();
    }
 
    public static void printCodes(HuffmanTree tree, StringBuffer prefix) {
        assert tree != null;
        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;
			String s = ""+leaf.value; String s1 = prefix.toString();int s2 = leaf.frequency;
            // print out character, frequency, and code for this leaf (which is just the prefix)
			dict1.put(s,s1);
			dict2.put(s,s2);
            System.out.println(leaf.value + "\t" + String.format("%09d",Integer.parseInt(Integer.toBinaryString((int)leaf.value))) + "\t" + leaf.frequency + "\t" + prefix);
        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;
 
            // traverse left
            prefix.append('0');
            printCodes(node.left, prefix);
            prefix.deleteCharAt(prefix.length()-1);
 
            // traverse right
            prefix.append('1');
            printCodes(node.right, prefix);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }
	public static void cr(){
		int abc=0;int def=0;
		for(int d=0;d<hexchars.length;d++){
			if(dict1.containsKey(hexchars[d])){		//getting length of binary data from Huffman encoding
				abc = abc+ (dict1.get(hexchars[d])).length()*(dict2.get(hexchars[d]));
			}
		}
		abc=abc/8;		//converting size to bytes
		def = everything.length();		//getting length of rgb hex encoded in ASCII(8-bits for each char)
		float CR = 100*abc/def;
		System.out.println("RGB file size(ASCII encoded)        : "+def+"b");
		System.out.println("Size of Huffman encoded binary data : "+abc+"b");
		System.out.println("Compression factor                  : "+CR+"% ");
	}
	public static void recon() throws IOException{
			String line = "";
			BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			int [][] mat = new int[image.getWidth()][image.getHeight()];
			int count=0;
			String[] arr;
			while((line=bf.readLine())!=null)
			{
				arr = line.split(" ");
				for(int i = 0; i<image.getHeight(); i++)
				{
					mat[count][i]= Integer.parseInt(arr[i],16);
					Color gColor = new Color(mat[count][i], mat[count][i], mat[count][i],255);
					grayscaleImage.setRGB(count,i, gColor.getRGB());
				}
				count++;
			}
		ImageIO.write(grayscaleImage, "png", new File("final.png"));
	}
	public static void decode(HuffmanTree t) throws IOException {
		assert t != null;
		if (t instanceof HuffmanLeaf) {
			HuffmanLeaf leaf = (HuffmanLeaf)t;  String s = ""+leaf.value; 
			out.writeBytes(s);
			decode((HuffmanNode)tree1);
		} else if (t instanceof HuffmanNode) {
			HuffmanNode node = (HuffmanNode)t;
				while((ch=inp.read()) != -1){
					char currcar = (char)ch;
					if(currcar=='0'){
						decode(node.left);				// traverse left
					}
					else if(currcar=='1'){
						decode(node.right);			// traverse right
					}
				}
		}
		
	}
	public static String pix() throws IOException {
		everything = "";
		System.out.print("Enter the file name with extension : ");
		sc = new Scanner(System.in);
		String name = sc.nextLine();
		File file= new File(name);
		image = ImageIO.read(file);
		PrintWriter writer = new PrintWriter("rgb.txt", "UTF-8"); 
		// Getting pixel color by position x and y 
		for(int x=0;x<image.getWidth();x++)
		{
			for(int y=0;y<image.getHeight();y++)
			{
				int clr=  image.getRGB(x,y); 
				int  blue  =  clr & 0x000000ff;
				writer.print(Integer.toHexString(blue));
				writer.print(" ");
			}
			writer.println("");
		}writer.close();

		BufferedReader br = new BufferedReader(new FileReader("rgb.txt"));
		try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

					while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
					  }
					everything = sb.toString();
			}
		 finally {
				br.close();
			}
		return everything;
	}
    public static void main(String[] args) throws IOException {
        String test = pix();
        // we will assume that all our characters will have
        // code less than 256, for simplicity (i.e, for a B/W image only)
        int[] charFreqs = new int[256];
        // read each character and record the frequencies
        for (char c : test.toCharArray())
            charFreqs[c]++;
        tree = buildTree(charFreqs);
		tree2 = tree;		
		tree1=tree2;
        // print out results
        System.out.println("SYMBOL\tASCII\t\tWEIGHT\tHUFFMAN CODE");
        printCodes(tree, new StringBuffer());
		int c; 
		DataInputStream in = new DataInputStream(new FileInputStream("rgb.txt"));
		DataOutputStream o = new DataOutputStream(new FileOutputStream("bin.txt"));
		try{
			while((c=in.read())!=-1)
			{
				char currentchar =(char)c;
				String s = ""+currentchar;
				if(dict1.containsKey(s))
				{
					String data = (String)dict1.get(s);
					o.writeBytes(data);
				}
			}
		}
		finally {
			in.close(); o.close();
		}   
//		System.out.println(dict1+"\n"+dict2);
		cr();
		inp = new DataInputStream(new FileInputStream("bin.txt"));
		out =  new DataOutputStream(new FileOutputStream("recovered.txt"));
		try{
		decode(tree2);
		}
		finally{
		inp.close();out.close();
		}
		RIn = new FileInputStream("rgb.txt");
		scanner = new Scanner(RIn);
		x = new DataInputStream(RIn);
		bf = new BufferedReader(new InputStreamReader(x));
		try{
			recon();
		}finally{
			RIn.close();x.close();bf.close();
		}
	}
}