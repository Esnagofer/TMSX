package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.components.editor.sourcecodearea.view;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.EditableStyledDocument;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class SourceCodeArea extends CodeArea {

    private static final String[] COMPILER_KEYWORDS = new String[] {
        "IFNDEF", "ENDIF", "INCLUDE", "DEFINE", "MODULE", "ENDMODULE", 
        "OUTPUT", "EQU", "macro", "endmacro"
    };

    private static final String[] Z80_KEYWORDS = new String[] {
        "call", "im", "di", "ei", "add", "adc", "sub", "sbc", "jp", "jr", "cp", "halt",
        "and", "rr", "ret", "db", "import", "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
        "or", "xor", "this", "throw", "throws", "and", "pop", "inc", "ld", "push"
    };

    private static final String[] Z80_REGISTERS = new String[] {
    	",a", "a,", "[\\s]*a[\\s]*", "a,[\\s]*\\(", "\\)[\\s]*,a",	
    	",b", "b,", "[\\s]*b[\\s]*", "b,[\\s]*\\(", "\\)[\\s]*,b",	
    	",c", "c,", "[\\s]*c[\\s]*", "c,[\\s]*\\(", "\\)[\\s]*,c",	
    	",d", "d,", "[\\s]*d[\\s]*", "d,[\\s]*\\(", "\\)[\\s]*,d",	
    	",e", "e,", "[\\s]*e[\\s]*", "e,[\\s]*\\(", "\\)[\\s]*,e",	
    	",h", "h,", "[\\s]*h[\\s]*", "h,[\\s]*\\(", "\\)[\\s]*,h",	
    	",l", "l,", "[\\s]*l[\\s]*", "l,[\\s]*\\(", "\\)[\\s]*,l",	
    	",hl", "hl,", "[\\s]*hl[\\s]*", "hl,[\\s]*\\(", "\\)[\\s]*,hl",
    	",de", "de,", "[\\s]*de[\\s]*", "de,[\\s]*\\(", "\\)[\\s]*,de",
    	",bc", "bc,", "[\\s]*bc[\\s]*", "bc,[\\s]*\\(", "\\)[\\s]*,bc",
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
        
	protected SourceCodeArea() {
		init();
	}

	protected SourceCodeArea(EditableStyledDocument<Collection<String>, String, Collection<String>> document) {
		super(document);
		init();
	}

	protected SourceCodeArea(String text) {
		super(text);
		init();
		replaceText(0, 0, text);
	}
	
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
	
	private void init() {
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        this.multiPlainChanges()
	        .successionEnds(Duration.ofMillis(10))
	        .subscribe(ignore -> this.setStyleSpans(0, computeHighlighting(getText())));
        setStyle("-fx-font-family: consolas; -fx-font-size: 10pt;");
	}

	public static SourceCodeArea valueOf() {
		return new SourceCodeArea();
	}

	public static SourceCodeArea valueOf(EditableStyledDocument<Collection<String>, String, Collection<String>> document) {
		return new SourceCodeArea(document);
	}
	

	public static SourceCodeArea valueOf(String text) {
		return new SourceCodeArea(text);
	}

	
}
