package com.irisine.jiris.compiler.assistpart

import java.util.LinkedList

class IrisSwitchBlock(val whenList: LinkedList<IrisWhen>, val elseBlock: IrisBlock? = null)
