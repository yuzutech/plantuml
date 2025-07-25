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
package net.sourceforge.plantuml.activitydiagram.command;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.abel.LinkArg;
import net.sourceforge.plantuml.activitydiagram.ActivityDiagram;
import net.sourceforge.plantuml.annotation.DeadCode;
import net.sourceforge.plantuml.classdiagram.command.CommandLinkClass;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.ParserPass;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.decoration.LinkDecor;
import net.sourceforge.plantuml.decoration.LinkType;
import net.sourceforge.plantuml.descdiagram.command.CommandLinkElement;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOptional;
import net.sourceforge.plantuml.regex.RegexOr;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.utils.Direction;
import net.sourceforge.plantuml.utils.LineLocation;

@DeadCode
public class CommandIf extends SingleLineCommand2<ActivityDiagram> {

	private CommandIf() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandIf.class.getName(), RegexLeaf.start(), //
				new RegexOptional(//
						new RegexOr("FIRST", //
								new RegexLeaf(1, "STAR", "(\\(\\*(top)?\\))"), //
								new RegexLeaf(1, "CODE", "([%pLN_.]+)"), //
								new RegexLeaf(1, "BAR", "(?:==+)[%s]*([%pLN_.]+)[%s]*(?:==+)"), //
								new RegexLeaf(1, "QUOTED", "[%g]([^%g]+)[%g](?:[%s]+as[%s]+([%pLN_.]+))?"))), //
				RegexLeaf.spaceZeroOrMore(), //
				// new RegexOptional(new RegexLeaf("ARROW",
				// "([=-]+(?:(left|right|up|down|le?|ri?|up?|do?)(?=[-=.]))?[=-]*\\>)")), //
				new RegexOptional(new RegexConcat( //
						new RegexLeaf(1, "ARROW_BODY1", "([-.]+)"), //
						new RegexLeaf(1, "ARROW_STYLE1", "(?:\\[(" + CommandLinkElement.LINE_STYLE + ")\\])?"), //
						new RegexLeaf(1, "ARROW_DIRECTION", "(\\*|left|right|up|down|le?|ri?|up?|do?)?"), //
						new RegexLeaf(1, "ARROW_STYLE2", "(?:\\[(" + CommandLinkElement.LINE_STYLE + ")\\])?"), //
						new RegexLeaf(1, "ARROW_BODY2", "([-.]*)"), //
						new RegexLeaf("\\>") //
				)), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexLeaf(1, "BRACKET", "\\[([^\\]*]+[^\\]]*)\\]")), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOr(//
						new RegexLeaf(1, "IF1", "if[%s]*[%g]([^%g]*)[%g][%s]*(?:as[%s]+([%pLN_.]+)[%s]+)?"), //
						new RegexLeaf(1, "IF2", "if[%s]+(.+?)")), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexLeaf("then")), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram diagram, LineLocation location, RegexResult arg,
			ParserPass currentPass) {
		final Entity entity1 = CommandLinkActivity.getEntity(location, diagram, arg, true);
		if (entity1 == null)
			return CommandExecutionResult.error("No if possible at this point");

		final String ifCode;
		final String ifLabel;
		if (arg.get("IF2", 0) == null) {
			ifCode = arg.get("IF1", 1);
			ifLabel = arg.get("IF1", 0);
		} else {
			ifCode = null;
			ifLabel = arg.get("IF2", 0);
		}
		diagram.startIf(location, ifCode);

		int lenght = 2;

		if (arg.get("ARROW_BODY1", 0) != null) {
//			final String arrow = StringUtils.manageArrowForCuca(arg.get("ARROW", 0));
//			lenght = arrow.length() - 1;
			final String arrowBody1 = CommandLinkClass.notNull(arg.get("ARROW_BODY1", 0));
			final String arrowBody2 = CommandLinkClass.notNull(arg.get("ARROW_BODY2", 0));
			final String arrowDirection = CommandLinkClass.notNull(arg.get("ARROW_DIRECTION", 0));

			final String arrow = StringUtils.manageArrowForCuca(arrowBody1 + arrowDirection + arrowBody2 + ">");
			lenght = arrow.length() - 1;
		}

		final Entity branch = diagram.getCurrentContext().getBranch();

		final LinkArg linkArg = LinkArg.build(Display.getWithNewlines(diagram.getPragma(), arg.get("BRACKET", 0)),
				lenght);
		Link link = new Link(location, diagram, diagram.getSkinParam().getCurrentStyleBuilder(), entity1, branch,
				new LinkType(LinkDecor.ARROW, LinkDecor.NONE), linkArg.withQuantifier(null, ifLabel)
						.withDistanceAngle(diagram.getLabeldistance(), diagram.getLabelangle()));
		if (arg.get("ARROW", 0) != null) {
			final Direction direction = StringUtils.getArrowDirection(arg.get("ARROW", 0));
			if (direction == Direction.LEFT || direction == Direction.UP)
				link = link.getInv();

		}

		link.applyStyle(arg.getLazzy("ARROW_STYLE", 0));
		diagram.addLink(link);

		return CommandExecutionResult.ok();
	}

}
