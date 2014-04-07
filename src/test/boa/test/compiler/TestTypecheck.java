package boa.test.compiler;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;

import boa.compiler.SymbolTable;
import boa.compiler.ast.Start;
import boa.compiler.visitors.TypeCheckingVisitor;

import boa.parser.BoaLexer;
import boa.parser.BoaParser;

@RunWith(JUnit4.class)
public class TestTypecheck extends BaseTest {
	final private static String rootDir = "test/typecheck/";
	final private static String badDir = rootDir + "errors/";

	@Test
	public void cout() throws IOException {
		compile(load(badDir + "cout.boa"), "type 'string' does not support the '<<' operator");
	}

	@Test
	public void assignTypeToVar() throws IOException {
		compile(load(badDir + "assign-type-to-var.boa"), "type 'Project' is not a value and can not be assigned");
	}

	@Test
	public void assignTypeToVar2() throws IOException {
		compile(load(badDir + "assign-type-to-var2.boa"), "type 'Project' is not a value and can not be assigned");
	}

	@Test
	public void varAsType() throws IOException {
		compile(load(badDir + "var-as-type.boa"), "type 'input' undefined");
	}


	///////////////////////////////////////////

	private void compile(final String input, final String error) throws IOException {
		final BoaParser parser = new BoaParser(new CommonTokenStream(new BoaLexer(new ANTLRInputStream(new StringReader(input)))));
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
		final Start p = parser.start().ast;

		try {
			new TypeCheckingVisitor().start(p, new SymbolTable());
			if (error != null)
				fail("expected error: " + error);
		} catch (final Exception e) {
			if (error == null)
				fail("found unexpected error: " + e.getMessage());
			else
				assertEquals(error, e.getMessage());
		}
	}
}
