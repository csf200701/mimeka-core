import java.io.File;

import com.igd.mimeka.MimeKa;
import com.igd.mimeka.io.FileUtils;

public class MimeTest {
	
	public static void testDetect() {
		MimeKa mimeKa = new MimeKa();
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\������\\Ͷ��\\��Ʊ\\��ֵͶ��\\��ָ֤���ɷֹ�-20211106.xls"))));
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\������\\1_����ҵ�100���ر��Ĺ�Ʊ.pdf"))));
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\������\\��ʱ��-���˼��� - 22.doc"))));
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\������\\Ͷ��\\��Ʊ\\ָ����ֵ��.docx")), "ָ����ֵ��.docx"));
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\projects\\igd-test\\src\\main\\java\\FileUtils.java"))));
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\������\\�ճ���֧\\git\\daily-z\\�ճ���֧-2022.xlsx")), "�ճ���֧-2022.xlsx"));
	}
	
	public static void main(String[] args) {
		testDetect();
	}

}
