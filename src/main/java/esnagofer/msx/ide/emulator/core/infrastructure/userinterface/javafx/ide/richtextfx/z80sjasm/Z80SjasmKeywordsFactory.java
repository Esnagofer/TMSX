package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.richtextfx.z80sjasm;


import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.z80.Z80;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Z80SjasmKeywordsFactory {

    private static final String[] COMPILER_KEYWORDS = new String[] {
        "IFNDEF", "ENDIF", "INCLUDE", "DEFINE", "MODULE", "ENDMODULE", 
        "OUTPUT",
        "EQU", "macro", "endmacro"
    };

    private static final String[] Z80_KEYWORDS = new String[] {
        "call", "im",
        "di", "ei", "add", "adc", "sub",
        "sbc", "jp", "jr", "cp", "halt",
        "and", "rr", "ret", "db", "import",
        "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super",
        "or", "xor", "this", "throw", "throws",
        "and", "pop", "inc", "ld", "push"
    };

    private static final String[] Z80_REGISTERS = new String[] {
    	",a", "a,", "[\\s]*a[\\s]*", "a,[\\s]*\\(", "\\)[\\s]*,a",	
    	",b", "b,", " b ",	
    	",c", "c,", " c ",	
    	",d", "d,", " d ",	
    	",e", "e,", " e ",	
    	",h", "h,", " h ",	
    	",l", "l,", " l ",	
    	",hl", "hl,", " hl ", "(hl)"
    };

    private static final String COMPILER_KEYWORD_PATTERN = "\\b(" + String.join("|", COMPILER_KEYWORDS) + ")\\b";
    private static final String Z80_KEYWORD_PATTERN = "\\b(" + String.join("|", Z80_KEYWORDS) + ")\\b";
    private static final String Z80_REGISTER_PATTERN = "\\b(" + String.join("|", Z80_REGISTERS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;(.)*";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String HEX_NUMBER_PATTERN = "0x([0-9abcdefABCDEF]+)";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<COMPILERKEYWORD>" + COMPILER_KEYWORD_PATTERN + ")"
            + "|(?<Z80KEYWORD>" + Z80_KEYWORD_PATTERN + ")"
            + "|(?<Z80REGISTER>" + Z80_REGISTER_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            + "|(?<HEXNUMBER>" + HEX_NUMBER_PATTERN + ")"
    );
    
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
    	Matcher matcher = PATTERN.matcher(text);
    	int lastKwEnd = 0;
    	StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
    	while(matcher.find()) {
    		String styleClass =
				matcher.group("COMPILERKEYWORD") != null ? "compilerkeyword" :
				matcher.group("Z80KEYWORD") != null ? "z80keyword" :
				matcher.group("Z80REGISTER") != null ? "z80register" :
				matcher.group("PAREN") != null ? "paren" :
				matcher.group("BRACE") != null ? "brace" :
				matcher.group("BRACKET") != null ? "bracket" :
				matcher.group("SEMICOLON") != null ? "comment" :
				matcher.group("STRING") != null ? "string" :
				matcher.group("COMMENT") != null ? "comment" :
				matcher.group("HEXNUMBER") != null ? "hexnumber" :
				null; /* never happens */ 
    		assert styleClass != null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
    	}
    	spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
    	return spansBuilder.create();
    }

    public static CodeArea create(Scene scene, String text) {
        CodeArea codeArea = new CodeArea();
        // add line numbers to the left of area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        // recompute the syntax highlighting 500 ms after user stops editing area
        Subscription cleanupWhenNoLongerNeedIt = codeArea
	        // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
	        // multi plain changes = save computation by not rerunning the code multiple times
	        //   when making multiple changes (e.g. renaming a method at multiple parts in file)
	        .multiPlainChanges()
	
	        // do not emit an event until 500 ms have passed since the last emission of previous stream
	        .successionEnds(Duration.ofMillis(10))
	
	        // run the following code block when previous stream emits an event
	        .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
	        // when no longer need syntax highlighting and wish to clean up memory leaks
	        // run: `cleanupWhenNoLongerNeedIt.unsubscribe();`
        codeArea.replaceText(0, 0, text);
        scene.getStylesheets().add(Z80SjasmKeywordsFactory.class.getResource("/css/z80sjasm-keywords.css").toExternalForm());
        return codeArea;
    }
}