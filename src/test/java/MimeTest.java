import java.io.File;

import com.igd.mimeka.MimeKa;
import com.igd.mimeka.io.FileUtils;

public class MimeTest {
	
	public static void testDetect() {
		MimeKa mimeKa = new MimeKa();
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\工作区\\投资\\股票\\价值投资\\上证指数成分股-20211106.xls"))));
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\工作区\\1_如何找到100倍回报的股票.pdf"))));
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\工作区\\陈时峰-个人简历 - 22.doc"))));
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\工作区\\投资\\股票\\指数估值表.docx")), "指数估值表.docx"));
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\projects\\igd-test\\src\\main\\java\\FileUtils.java"))));
		System.out.println(mimeKa.detect(FileUtils.file2byte(new File("D:\\工作区\\日常开支\\git\\daily-z\\日常开支-2022.xlsx")), "日常开支-2022.xlsx"));
	}
	
	public static void main(String[] args) {
		testDetect();
	}

}
