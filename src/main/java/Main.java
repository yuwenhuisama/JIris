import org.irislang.jiris.compiler.IrisInterpreter;

import com.irisine.jiris.compiler.IrisCompiler;

import java.util.ArrayList;
import java.util.Iterator;


public class Main {
	
	static final boolean PARSER_TEST = false;
	
	public static void main(String[] argv) throws Throwable {

//		ArrayList<String> list = new ArrayList<String>();
//		list.add("A");
//		list.add("B");
//		list.add("C");
//		list.add("D");
//		list.add("E");
//
//		Iterator<String> iter1 = list.iterator();
//		while(iter1.hasNext()) {
//			String var1 = iter1.next();
//			Iterator<String> iter2 = list.iterator();
//			while(iter2.hasNext()) {
//				String var2 = iter2.next();
//				System.out.print(var1 + "," + var2 + " ");
//			}
//			System.out.print("\n");
//		}

		if(!IrisInterpreter.INSTANCE.Initialize()) {
			IrisInterpreter.INSTANCE.ShutDown();
		}

		if(PARSER_TEST) {
			IrisCompiler.INSTANCE.TestLoad("src/test/resources/test.ir");
		} else {
			if(!IrisCompiler.INSTANCE.LoadScriptFromPath("src/test/resources/test.ir")){
				return;
			}

			IrisInterpreter.INSTANCE.setCurrentCompiler(IrisCompiler.INSTANCE);

			IrisInterpreter.INSTANCE.Run();

 			IrisInterpreter.INSTANCE.ShutDown();
		}

		//VerifyBrace(null);
	}
	
/*	static private void VerifyBrace(ArrayList<String> irisScriptText) throws Exception {
		// 检查大括号是不是写在下一行
		for(String line : irisScriptText) {
			// 检查一行代码是否以大括号开头，如果是则抛出异常，停止编译
			if(line.trim().startsWith("{")) {
				throw(new Exception("Stupid code, you motherfucker."));
			}
		}
	}*/
}
