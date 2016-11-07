/**************************************************************************
 *  Copyright notice
 *	
 *  ediarum - an Oxygen XML Author framework for digital scholarly editions
 *  Copyright (C) 2013 Berlin-Brandenburg Academy of Sciences and Humanities
 *	
 *  This file is part of ediarum; ediarum is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ediarum is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with ediarum.  If not, see <http://www.gnu.org/licenses/>.
***************************************************************************/

/**
 * InsertRegisterOperation.java - is a class to surround a selection with a register element.
 * It belongs to package ro.sync.ecss.extensions.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung). 
 * @author Martin Fechner
 * @version 1.0.3
 */
package org.bbaw.telota.ediarum;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import java.awt.Frame;

public class InsertRegisterOperation implements AuthorOperation{
	/**
	 * Argument describing the url.
	 */
	private static final String ARGUMENT_URL = "URL";

	/**
	 * Argument describing the node.
	 */
	private static final String ARGUMENT_NODE = "node";

	/**
	 * Argument describing the expression.
	 */
	private static final String ARGUMENT_EXPRESSION = "expression";

	/**
	 * Argument describing the insertNode.
	 */
	private static final String ARGUMENT_ELEMENT = "element";

	/**
	 * Arguments.
	 */
	private static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
		new ArgumentDescriptor(
				ARGUMENT_URL,
				ArgumentDescriptor.TYPE_STRING,
				"NEU: Die URL der Registerdatei, etwa: " +
				"http://user:passwort@www.example.com:port/exist/webdav/db/register.xml"),
		new ArgumentDescriptor(
				ARGUMENT_NODE,
				ArgumentDescriptor.TYPE_STRING,
				"Der XPath-Ausdruck zu den w�hlbaren Elementen, etwa: //person"),
		new ArgumentDescriptor(
				ARGUMENT_EXPRESSION,
				ArgumentDescriptor.TYPE_STRING,
				"Der in der Auswahlliste erscheinende Ausdruck mit Sub-Elementen " +
				"(dabei stehen Strings in \"\", Attribute beginnen mit @, Elemente mit /," +
				" Subelemente mit // und X-Path-Ausdr�cke mit .), etwa: " +
				"/name+\", \"+/vorname+\" \"+/lebensdaten"),
		new ArgumentDescriptor(
				ARGUMENT_ELEMENT,
				ArgumentDescriptor.TYPE_STRING,
				"Das an der Textstelle einzuf�gende Element, etwa: " +
				"\"<persName xmlns='http://www.tei-c.org/ns/1.0' key='\" + @id + \"' />\"")
	};

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
	 */
	public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
		// Die �bergebenen Argumente werden eingelesen ..
		Object urlArgVal = args.getArgumentValue(ARGUMENT_URL);
		Object nodeArgVal = args.getArgumentValue(ARGUMENT_NODE);
		Object expressionArgVal = args.getArgumentValue(ARGUMENT_EXPRESSION);
		Object elementArgVal = args.getArgumentValue(ARGUMENT_ELEMENT);
		// .. und �berpr�ft.
		if (urlArgVal != null
				&& urlArgVal instanceof String
				&& nodeArgVal != null
				&& nodeArgVal instanceof String
				&& expressionArgVal != null
				&& expressionArgVal instanceof String
				&& elementArgVal != null
				&& elementArgVal instanceof String) {
			// Wenn im aktuellen Dokument nichts selektiert ist, wird das aktuelle Wort ausgew�hlt.
			if (!authorAccess.getEditorAccess().hasSelection()) {
				authorAccess.getEditorAccess().selectWord();
			}
			int selStart = authorAccess.getEditorAccess().getSelectionStart();
			int selEnd = authorAccess.getEditorAccess().getSelectionEnd()-1;

			// F�r die sp�tere Verwendung werden die Variablen f�r die Registereintr�ge und IDs erzeugt.
			String[] eintrag = null, id = null;
			
			// Dann wird das Registerdokument eingelesen, wobei auf die einzelnen Registerelement und ..
			// .. die Ausdr�cke f�r die Eintr�ge und IDs R�cksicht genommen wird.
			ReadRegister register = new ReadRegister((String)urlArgVal, (String) nodeArgVal, (String) expressionArgVal, (String) elementArgVal);
			// Die Arrays f�r die Eintr�ge und IDs werden an die lokalen Variablen �bergeben.
			eintrag = register.getEintrag();
			id = register.getID();

			// Daf�r wird der RegisterDialog ge�ffnet und erh�lt die Eintr�ge und IDs als Parameter.
			InsertRegisterDialog RegisterDialog = new InsertRegisterDialog((Frame) authorAccess.getWorkspaceAccess().getParentFrame(), eintrag, id);
			// Wenn in dem Dialog ein Eintrag ausgew�hlt wurde, .. 
			if (!RegisterDialog.getSelectedID().isEmpty()){
				// wird im aktuellen Dokument um die Selektion das entsprechende Element mit ID eingesetzt.
				authorAccess.getDocumentController().surroundInFragment(RegisterDialog.getSelectedID(), selStart, selEnd);
			}
		} else {
			throw new IllegalArgumentException(
					"One or more of the argument values are not declared, they are: url - " + urlArgVal 
					+ ", node - " + nodeArgVal + ", expression - " + expressionArgVal + ", element - " + elementArgVal);
		}
	}


	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
	 */
	public ArgumentDescriptor[] getArguments() {
		return ARGUMENTS;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
	 */
	public String getDescription() {
		return "�ffnet einen Dialog, in welchem Eintr�ge eines Registers" +
				" ausgew�hlt werden kann. Die entsprechende ID wird an der markierten" +
				" Stelle eingef�gt.";
	}
}




