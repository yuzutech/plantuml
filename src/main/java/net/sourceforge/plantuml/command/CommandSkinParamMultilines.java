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
package net.sourceforge.plantuml.command;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.regex.Matcher2;
import net.sourceforge.plantuml.regex.Pattern2;
import net.sourceforge.plantuml.utils.BlocLines;

public class CommandSkinParamMultilines extends CommandMultilinesBracket<TitledDiagram> {

	private static final Pattern2 COMMENT_SINGLE_LINE = Pattern2.cmpile(CommandMultilinesComment.COMMENT_SINGLE_LINE);
	public static final CommandSkinParamMultilines ME = new CommandSkinParamMultilines();

	private CommandSkinParamMultilines() {
		super("^skinparam[%s]*(?:[%s]+([\\w.]*(?:\\<\\<.*\\>\\>)?[\\w.]*))?[%s]*\\{$");
	}

	@Override
	protected boolean isLineConsistent(String line, int level) {
		line = StringUtils.trin(line);
		if (hasStartingQuote(line))
			return true;

		return SkinLoader.p1.matcher(line).matches();
	}

	private boolean hasStartingQuote(CharSequence line) {
		return COMMENT_SINGLE_LINE.matcher(line).matches();
	}

	public CommandExecutionResult execute(TitledDiagram diagram, BlocLines lines, ParserPass currentPass) {
		final SkinLoader skinLoader = new SkinLoader(diagram);

		lines = lines.expandsNewline(true);

		final Matcher2 mStart = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
		if (mStart.find() == false)
			return CommandExecutionResult.error("Bad syntax for skinparam");

		final String group1 = mStart.group(1);
		diagram.setSkinParamUsed(true);

		return skinLoader.execute(lines, group1);

	}

	@Override
	public boolean isEligibleFor(ParserPass pass) {
		return pass == ParserPass.ONE;
	}

}
