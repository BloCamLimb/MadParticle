package cn.ussshenzhou.madparticle.command.inheritable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * @author USS_Shenzhou
 */
public class InheritableStringReader extends StringReader {

    public InheritableStringReader(StringReader other) {
        super(other);
    }

    public InheritableStringReader(String string) {
        super(string);
    }

    public static boolean isAllowedInheritableNumber(char c) {
        return StringReader.isAllowedNumber(c) || c == '=';
    }


    @Override
    public int readInt() throws CommandSyntaxException {
        final int start = getCursor();
        while (canRead() && isAllowedInheritableNumber(peek())) {
            skip();
        }
        final String number = getString().substring(start, getCursor());
        if (number.contains("=")) {
            return Integer.MAX_VALUE;
        }
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt().createWithContext(this);
        }
        try {
            return Integer.parseInt(number);
        } catch (final NumberFormatException ex) {
            setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(this, number);
        }
    }

    @Override
    public float readFloat() throws CommandSyntaxException {
        final int start = getCursor();
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }
        final String number = getString().substring(start, getCursor());
        if (number.contains("=")) {
            return Float.MAX_VALUE;
        }
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat().createWithContext(this);
        }
        try {
            return Float.parseFloat(number);
        } catch (final NumberFormatException ex) {
            setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(this, number);
        }
    }
}