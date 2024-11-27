/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 *
 *
 */
package net.sourceforge.plantuml.cheneer.command;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.cheneer.ChenEerDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.ParserPass;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.klimt.color.ColorParser;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.plasma.Quark;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOptional;
import net.sourceforge.plantuml.regex.RegexOr;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandCreateEntity extends SingleLineCommand2<ChenEerDiagram> {

	public CommandCreateEntity() {
		super(getRegexConcat());
	}

	protected static IRegex getRegexConcat() {
		return RegexConcat.build(CommandCreateEntity.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "(entity|relationship)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexOptional( // Copied from CommandCreatePackageBlock
						new RegexConcat( //
								new RegexLeaf("DISPLAY", "[%g]([^%g]+)[%g]"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("as"), //
								RegexLeaf.spaceOneOrMore() //
						)), //
				new RegexLeaf("CODE", "([%pLN_.]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(<<.+>>)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\{"), //
				RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	@Override
	protected CommandExecutionResult executeArg(ChenEerDiagram diagram, LineLocation location, RegexResult arg, ParserPass currentPass)
			throws NoSuchColorException {
		LeafType type;
		switch (arg.get("TYPE", 0)) {
			case "entity":
				type = LeafType.CHEN_ENTITY;
				break;
			case "relationship":
				type = LeafType.CHEN_RELATIONSHIP;
				break;
			default:
				throw new IllegalStateException();
		}

		final String idShort = arg.get("CODE", 0);
		final Quark<Entity> quark = diagram.quarkInContext(true, diagram.cleanId(idShort));
		String displayText = arg.get("DISPLAY", 0);
		if (displayText == null)
			displayText = quark.getName();

		final String stereo = arg.get("STEREO", 0);

		Entity entity = quark.getData();

		if (entity == null) {
			Display display = Display.getWithNewlines(diagram.legacyReplaceBackslashNByNewline(), displayText);
			entity = diagram.reallyCreateLeaf(quark, display, type, null);
		} else {
			if (entity.muteToType(type, null) == false)
				return CommandExecutionResult.error("Bad name");
		}

		if (stereo != null) {
			entity.setStereotype(Stereotype.build(stereo));
			entity.setStereostyle(stereo);
		}

		entity.setColors(color().getColor(arg, diagram.getSkinParam().getIHtmlColorSet()));

		diagram.pushOwner(entity);

		return CommandExecutionResult.ok();
	}

}
