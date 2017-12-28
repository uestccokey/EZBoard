/*
 * Copyright (C) 2001 by Dave Jarvis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * Online at: http://www.gnu.org/copyleft/gpl.html
 */

package com.barrybecker4.ca.dj.jigo.sgf.tokens;

import com.barrybecker4.ca.dj.jigo.sgf.SGFException;

import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * A generic token.  This class is responsible for parsing simple text --
 * nearly all SGFTokens subclass TextToken to get their content parsed.  The
 * subclasses then need only interpret the text read by parseContent.
 */
public class TextToken extends SGFToken {
    private String myText = null;

    public TextToken() { }

    /**
     * Reads the text in between the first opening '[' and closing ']'.  This
     * takes into consideration carriage returns, new lines, escaping ('\'),
     * and special punctuation.
     *
     * @param st - The stream which contains characters from an SGF file.
     * @return false - The content could not be parsed.
     */
    protected boolean parseContent(StreamTokenizer st)
            throws IOException, SGFException {
        int token = 0,
                prevToken = 0;
        StringBuffer sb = new StringBuffer();

        while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
            switch (token) {
                // Backslashes are "escape characters" and must be interpreted.
                //
                case (int) '\\':
                    token = st.nextToken();

                    // Ignores TT_EOL.
                    //
                    switch (token) {
                        // EEP!
                        //
                        case StreamTokenizer.TT_EOF:
                            return false;

                        case StreamTokenizer.TT_WORD:
                            sb.append(st.sval);
                            break;

                        // An escaped escape character or escaped closing '\' must
                        // be added as their literal equivalent.
                        //
                        case (int) '\\':
                        case (int) ']':
                            sb.append((char) token);
                            break;
                    }

                    break;

                case (int) ']':
                    setText(sb.toString().trim());
                    return true;

                case StreamTokenizer.TT_EOL:
                    sb.append('\n');
                    break;

                case StreamTokenizer.TT_WORD:
                    if (prevToken == StreamTokenizer.TT_WORD)
                        sb.append(' ');

                    sb.append(st.sval);
                    break;

                default:
                    sb.append((char) token);
                    break;
            }

            prevToken = token;
        }

        return false;
    }

    /**
     * Returns the entire text between the opening '[' and closing ']'.
     */
    public String getText() { return myText; }

    private void setText(String text) { myText = text; }
}

