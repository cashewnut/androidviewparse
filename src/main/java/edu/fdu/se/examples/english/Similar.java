package edu.fdu.se.examples.english;

/**
 * Created by Administrator on 2018-01-20.
 */
import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.Lin;

public class Similar {

	private String str1;
	private String str2;
	private String dir = "/usr/local/WordNet";
	private JWS ws = new JWS(dir, "2.1");

	public Similar(String str1, String str2) {
		this.str1 = str1;
		this.str2 = str2;
	}

	public double getSimilarity() {
		String[] strs1 = splitString(str1);
		String[] strs2 = splitString(str2);
		double sum = 0.0;
		for (String s1 : strs1) {
			for (String s2 : strs2) {
				double sc = maxScoreOfLin(s1, s2);
				sum += sc;
				System.out.println("当前计算: " + s1 + " VS " + s2 + " 的相似度为:" + sc);
			}
		}
		double Similarity = sum / (strs1.length * strs2.length);
		sum = 0;
		return Similarity;
	}

	private String[] splitString(String str) {
		String[] ret = str.split(" ");
		return ret;
	}

	private double maxScoreOfLin(String str1, String str2) {
		Lin lin = ws.getLin();
		double sc = lin.max(str1, str2, "n");
		if (sc == 0) {
			sc = lin.max(str1, str2, "v");
		}
		return sc;
	}

}
