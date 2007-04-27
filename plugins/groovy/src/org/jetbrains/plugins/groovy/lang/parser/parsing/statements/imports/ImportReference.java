/*
 *  Copyright 2000-2007 JetBrains s.r.o.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.jetbrains.plugins.groovy.lang.parser.parsing.statements.imports;

import com.intellij.lang.PsiBuilder;
import org.jetbrains.plugins.groovy.GroovyBundle;
import org.jetbrains.plugins.groovy.lang.lexer.GroovyElementType;
import org.jetbrains.plugins.groovy.lang.parser.GroovyElementTypes;
import org.jetbrains.plugins.groovy.lang.parser.parsing.util.ParserUtils;
import org.jetbrains.plugins.groovy.lang.parser.parsing.statements.typeDefinitions.ReferenceElement;

/**
 * Import identifier
 *
 * @author Ilya Sergey
 */
public class ImportReference implements GroovyElementTypes {

  public static GroovyElementType parse(PsiBuilder builder) {

    if (!mIDENT.equals(builder.getTokenType())) {
      return WRONGWAY;
    }

    ReferenceElement.parseForImport(builder);

    if (ParserUtils.lookAhead(builder, mDOT, mSTAR) ||
            ParserUtils.lookAhead(builder, mDOT, mNLS, mSTAR)) {
      ParserUtils.getToken(builder, mDOT);
      ParserUtils.getToken(builder, mNLS);
      ParserUtils.getToken(builder, mSTAR);
      return IMPORT_REFERENCE;
    }

    if (ParserUtils.lookAhead(builder, kAS)) {
      ParserUtils.getToken(builder, kAS);
      if (ParserUtils.lookAhead(builder, mNLS, mIDENT) ||
              ParserUtils.lookAhead(builder, mIDENT)) {
        ParserUtils.getToken(builder, mNLS);
        ParserUtils.getToken(builder, mIDENT);
      } else {
        builder.error(GroovyBundle.message("identifier.expected"));
      }
      return IMPORT_REFERENCE;
    }

    if (ParserUtils.lookAhead(builder, mDOT)) {
      builder.error(GroovyBundle.message("identifier.expected"));
    }

    return IMPORT_REFERENCE;

  }


}
