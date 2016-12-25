package com.irisine.jiris.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.irislang.jiris.compiler.IrisInterpreter;
import org.irislang.jiris.compiler.IrisNativeJavaClass;
import org.irislang.jiris.compiler.IrisRunnable;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.dev.IrisDevUtil;

import com.irisine.jiris.compiler.assistpart.IrisBlock;
import com.irisine.jiris.compiler.assistpart.IrisDeferredBlock;
import com.irisine.jiris.compiler.parser.IrisParser;
import com.irisine.jiris.compiler.parser.ParseException;
import com.irisine.jiris.compiler.statement.IrisStatement;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.*;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public enum IrisCompiler {
	
	INSTANCE;
	
	public enum CurrentDefineType {
		Normal,
		Class,
		Module,
		Interface,
	}
	
	private String m_currentFile = "";
	private ByteBuddy m_curretByteBuddy = null; 
	private String m_currentClassName = "";
		
	private LinkedList<IrisStatement> m_statements = new LinkedList<IrisStatement>();
	private Builder<IrisNativeJavaClass> m_currentBuilder = null;
	
	private LinkedList<String> m_uniqueStrings = new LinkedList<String>();
	private HashMap<String, Integer> m_uniqueStringHash = new HashMap<String, Integer>();
	
	private Queue<IrisDeferredBlock> m_deferredStatements = new LinkedList<IrisDeferredBlock>();
	private HashMap<String, Integer> m_blockNameCount = new HashMap<String ,Integer>();
	
	private Class<?> m_javaClass = null;
	
	private CurrentDefineType m_currentDefineType = CurrentDefineType.Normal;
	private String m_currentDefineName = "";
	
	private boolean m_staticDefine = false;
	
	private Label m_currentEndLable = null;
	
	private boolean m_firstStackFrameGenerated = false;
	
	public boolean isFirstStackFrameGenerated() {
		return m_firstStackFrameGenerated;
	}
	
	public void setFirstStackFrameGenerated(boolean firstStackFrameGenerated) {
		m_firstStackFrameGenerated = firstStackFrameGenerated;
	}
	
	public Label getCurrentEndLable() {
		return m_currentEndLable;
	}
	
	private void setCurrentEndLable(Label currentEndLable) {
		m_currentEndLable = currentEndLable;
	}
	
	public void setStaticDefine(boolean staticDefine) {
		m_staticDefine = staticDefine;
	}
	
	public int GetIndexOfContextVar() {
		return m_staticDefine ? 0 : 1;
	}
	
	public int GetIndexOfThreadInfoVar() {
		return GetIndexOfContextVar() + 1;
	}
	
	public int GetIndexOfResultValue() {
		return GetIndexOfContextVar() + 2;
	}
	
	public CurrentDefineType getCurrentDefineType() {
		return m_currentDefineType;
	}
	
	public void setCurrentDefineType(CurrentDefineType currentDefineType) {
		m_currentDefineType = currentDefineType;
	}
	
	public String getCurrentDefineName() {
		return m_currentDefineName;
	}
	
	public void setCurrentDefineName(String currentDefineName) {
		m_currentDefineName = currentDefineName;
	}
	
	public int GetBlockNameCount(String name) {
		Integer count = m_blockNameCount.get(name);
		if(count == null) {
			m_blockNameCount.put(name, 1);
			return 1;
		}
		else {
			m_blockNameCount.put(name, count + 1);
			return count;
		}
	}
	
	public void PushDeferredStatement(IrisDeferredBlock statement) {
		m_deferredStatements.add(statement);
	}
	
	public IrisDeferredBlock PopDeferredStatement() {
		return m_deferredStatements.poll();
	}
	
	public boolean IsDeferredStatementQueueEmpty() {
		return m_deferredStatements.isEmpty();
	}
		
	public void AddUniqueString(String str) {
		if(!m_uniqueStringHash.containsKey(str)) {
			m_uniqueStrings.add(str);
			m_uniqueStringHash.put(str, m_uniqueStrings.size() - 1);
		}
	}
	
	public int GetUinqueIndex(String str){ 
		return m_uniqueStringHash.get(str);
	}
	
	public void AddStatement(IrisStatement statement) {
		m_statements.add(statement);
	}
	
	public LinkedList<IrisStatement> getStatements() {
		return m_statements;
	}
	
	public Builder<IrisNativeJavaClass> getCurrentBuilder() {
		return m_currentBuilder;
	}
	
	public String getCurrentClassName() {
		return m_currentClassName;
	}
	
	private boolean Generate() {
		
		int index = m_currentFile.lastIndexOf(".");
		IrisInterpreter.INSTANCE.InceamJavaClassFileNumber();
		m_currentClassName = m_currentFile.substring(0, index) + "$" + "ir";
		
		m_curretByteBuddy = new ByteBuddy();
		m_currentBuilder = m_curretByteBuddy
				.subclass(IrisNativeJavaClass.class)
				.implement(IrisRunnable.class)
				.visit(new AsmVisitorWrapper(){
			@Override
			public int mergeReader(int arg0) {
				return 0;
			}

			@Override
			public int mergeWriter(int arg0) {
				return ClassWriter.COMPUTE_FRAMES;
			}

			@Override
			public ClassVisitor wrap(TypeDescription arg0, ClassVisitor arg1, int arg2, int arg3) {
				return arg1;
			}
		}).name(m_currentClassName);
		
		m_currentBuilder = m_currentBuilder.defineField("sm_uniqueStringObjects", 
				ArrayList.class, 
				Visibility.PUBLIC, 
				FieldManifestation.FINAL, 
				Ownership.STATIC).initializer(new ByteCodeAppender(){

			@Override
			public Size apply(MethodVisitor mv, Context arg1, MethodDescription arg2) {
				mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
				mv.visitInsn(Opcodes.DUP);
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
				mv.visitFieldInsn(Opcodes.PUTSTATIC, m_currentClassName, "sm_uniqueStringObjects", "Ljava/util/ArrayList;");
				mv.visitInsn(Opcodes.RETURN);
				return new Size(0, 0);
			}
		});
		
		ReceiverTypeDefinition<IrisNativeJavaClass> mainDefininition = GenerateIrisNativeJavaMethod("run", new Implementation() {
			@Override
			public InstrumentedType prepare(InstrumentedType arg0) {
				return arg0;
			}

			@Override
			public ByteCodeAppender appender(Target arg0) {
				return RunMethodAppender();
			}
		});
		
		while(!IsDeferredStatementQueueEmpty()) {
			IrisDeferredBlock deferredStatement = PopDeferredStatement();
			
			mainDefininition = mainDefininition.defineMethod(deferredStatement.getGenerateName(),
					IrisValue.class, 
					Visibility.PUBLIC, 
					Ownership.STATIC)
					.withParameters(IrisContextEnvironment.class, IrisThreadInfo.class)
					.throwing(Throwable.class)
					.intercept(new Implementation() {
						@Override
						public InstrumentedType prepare(InstrumentedType arg0) {
							return arg0;
						}

						@Override
						public ByteCodeAppender appender(Target arg0) {
							return UserMethodAppender(deferredStatement.getStatement());
						}
					});
		}
		
		Unloaded<IrisNativeJavaClass> classObj = mainDefininition.make();
		
		m_javaClass = classObj.load(getClass().getClassLoader()).getLoaded();
		InitUniqueStringList(m_javaClass);
		
		byte[] jvmcode = classObj.getBytes();
		
		File file = new File(m_currentClassName + ".class");
		if(file.exists()) {
			file.delete();
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			fos.write(jvmcode);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void InitUniqueStringList(Class<?> javaClass) {
		ArrayList<IrisValue> values = null;
		try {
			values = (ArrayList<IrisValue>) javaClass.getField("sm_uniqueStringObjects").get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(String uString : m_uniqueStrings) {
			values.add(IrisDevUtil.CreateUniqueString(uString));
		}
	}
	
	private ByteCodeAppender RunMethodAppender() {
		return new RunMethodAppender(this);
	}
	
	private ByteCodeAppender UserMethodAppender(IrisBlock block) {
		return new UserMethodAppender(this, block);
	}
	
	private ReceiverTypeDefinition<IrisNativeJavaClass> GenerateIrisNativeJavaMethod(String methodName, Implementation impl) {
		
		return m_currentBuilder.defineMethod(methodName, IrisValue.class, Visibility.PUBLIC)
						.withParameters(IrisContextEnvironment.class, IrisThreadInfo.class)
						.throwing(Throwable.class)
						.intercept(impl);
	}
	
	public boolean LoadScriptFromPath(String path) {
		m_currentFile = path;
		IrisParser parser = new IrisParser(path);
		try {
			parser.translation_unit();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
			parser.ReInit(System.in);
			return false;
		}
		catch (Error e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		return Generate();
	}
	
	public boolean TestLoad(String path) {
		IrisParser parser = new IrisParser(path);
		try {
			parser.translation_unit();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public Class<?> getJavaClass() {
		return m_javaClass;
	}
	
	private static class RunMethodAppender implements ByteCodeAppender {
		
		IrisCompiler m_compiler = null;
		
		public RunMethodAppender(IrisCompiler compiler) {
			m_compiler = compiler;
		}
		
		@Override
		public Size apply(MethodVisitor mv, Context arg1, MethodDescription arg2) {
			m_compiler.setFirstStackFrameGenerated(false);
			m_compiler.setStaticDefine(false);
			Label lableFrom = new Label();
			Label lableEnd = new Label();
			m_compiler.setCurrentEndLable(new Label());
			
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "Nil", "()Lorg/irislang/jiris/core/IrisValue;", false);
			mv.visitVarInsn(Opcodes.ASTORE, 3);
			
			mv.visitLabel(lableFrom);
			for(IrisStatement statement : m_compiler.getStatements()) {
				statement.Generate(m_compiler, m_compiler.getCurrentBuilder(), mv);
			}
			
			mv.visitLabel(m_compiler.getCurrentEndLable());
			
			mv.visitVarInsn(Opcodes.ALOAD, 3);
			mv.visitInsn(Opcodes.ARETURN);
			
			mv.visitLabel(lableEnd);
			m_compiler.setCurrentEndLable(null);
			
			mv.visitLocalVariable("this", "L" + m_compiler.getCurrentClassName() + ";", null, lableFrom, lableEnd, 0);
			mv.visitLocalVariable("context", "Lorg/irislang/jiris/core/IrisContextEnvironment;", null, lableFrom, lableEnd, 1);
			mv.visitLocalVariable("threadInfo", "Lorg/irislang/jiris/core/IrisThreadInfo;", null, lableFrom, lableEnd, 2);
			mv.visitLocalVariable("resultValue", "Lorg/irislang/jiris/core/IrisValue;", null, lableFrom, lableEnd, 3);
			
			return new Size(0, 0);
		}
		
	}
	
	private static class UserMethodAppender implements ByteCodeAppender {
		
		IrisCompiler m_compiler = null;
		IrisBlock m_block = null;
		
		public UserMethodAppender(IrisCompiler compiler, IrisBlock block) {
			m_compiler = compiler;
			m_block = block;
		}

		@Override
		public Size apply(MethodVisitor mv, Context arg1, MethodDescription arg2) {
			m_compiler.setFirstStackFrameGenerated(false);
			m_compiler.setStaticDefine(true);
			Label lableFrom = new Label();
			Label lableEnd = new Label();
			m_compiler.setCurrentEndLable(new Label());
			
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "Nil", "()Lorg/irislang/jiris/core/IrisValue;", false);
			mv.visitVarInsn(Opcodes.ASTORE, 2);
			
			mv.visitLabel(lableFrom);
			
			m_block.Generate(m_compiler, m_compiler.getCurrentBuilder(), mv);
			
			mv.visitLabel(m_compiler.getCurrentEndLable());
			m_compiler.setCurrentEndLable(null);
			
			mv.visitVarInsn(Opcodes.ALOAD, 2);
			mv.visitInsn(Opcodes.ARETURN);
			
			mv.visitLabel(lableEnd);
			
			mv.visitLocalVariable("context", "Lorg/irislang/jiris/core/IrisContextEnvironment;", null, lableFrom, lableEnd, 0);
			mv.visitLocalVariable("threadInfo", "Lorg/irislang/jiris/core/IrisThreadInfo;", null, lableFrom, lableEnd, 1);
			mv.visitLocalVariable("resultValue", "Lorg/irislang/jiris/core/IrisValue;", null, lableFrom, lableEnd, 2);
			
			return new Size(0, 0);
		}
		
	}
}
