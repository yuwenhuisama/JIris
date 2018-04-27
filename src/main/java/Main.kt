import org.irislang.jiris.IrisInterpreter

import com.irisine.jiris.compiler.IrisCompiler


object Main {

    internal val PARSER_TEST = false

    @Throws(Throwable::class)
    @JvmStatic
    fun main(argv: Array<String>) {
        if (!IrisInterpreter.INSTANCE.Initialize()) {
            IrisInterpreter.INSTANCE.ShutDown()
        }

        if (PARSER_TEST) {
            IrisCompiler.INSTANCE.TestLoad("src/test/resources/test.ir")
        } else {
            if (!IrisCompiler.INSTANCE.LoadScriptFromPath("src/test/resources/test.ir")) {
                return
            }

            IrisInterpreter.INSTANCE.currentCompiler = IrisCompiler.INSTANCE

            IrisInterpreter.INSTANCE.Run()

            IrisInterpreter.INSTANCE.ShutDown()
        }
    }
}
