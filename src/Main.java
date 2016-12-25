import java.util.ArrayList;

import org.irislang.jiris.compiler.IrisInterpreter;

import com.irisine.jiris.compiler.IrisCompiler;



public class Main {
	
	static final boolean TEST = false;
	
	public static void main(String[] argv) throws Throwable {
				
		if(!IrisInterpreter.INSTANCE.Initialize()) {
			IrisInterpreter.INSTANCE.ShutDown();
		}
		
		if(TEST) {
			IrisCompiler.INSTANCE.TestLoad("test.ir");			
		} else {
			if(!IrisCompiler.INSTANCE.LoadScriptFromPath("test.ir")){
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
