package com.irisine.jiris.compiler.parser;
import com.irisine.jiris.compiler.IrisSyntaxUnit;

public class IrisToken extends Token {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IrisToken(int kind, String image) {
	   this.kind = kind;
	   this.image = image;
	}
	
	private IrisSyntaxUnit m_syntaxUnit = null;
	
	public static final Token newToken(int ofKind, String tokenImage) {
	  return new IrisToken(ofKind, tokenImage);
	}

	public IrisSyntaxUnit getSyntaxUnit() {
		return m_syntaxUnit;
	}

	public void setSyntaxUnit(IrisSyntaxUnit syntaxUnit) {
		this.m_syntaxUnit = syntaxUnit;
	}
}
