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
 */
package net.sourceforge.plantuml.tim;

import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.text.StringLocated;
import net.sourceforge.plantuml.tim.expression.TValue;

public class EaterForeach extends Eater {

	private String varname;
	private JsonValue jsonValue;

	public EaterForeach(StringLocated s) {
		super(s);
	}

	@Override
	public void analyze(TContext context, TMemory memory) throws EaterException {
		skipSpaces();
		checkAndEatChar("!foreach");
		skipSpaces();
		this.varname = eatAndGetVarname();
		skipSpaces();
		checkAndEatChar("in");
		skipSpaces();
		final TValue value = eatExpression(context, memory);
		this.jsonValue = (JsonValue) value.toJson();
	}

	public boolean isSkip() {
		if (this.jsonValue == null)
			return true;

		return size(jsonValue) == 0;
	}

	public static int size(JsonValue value) {
		if (value instanceof JsonArray)
			return ((JsonArray) value).size();
		if (value instanceof JsonObject)
			return ((JsonObject) value).size();
		throw new IllegalArgumentException();
	}

	public final String getVarname() {
		return varname;
	}

	public final JsonValue getJsonValue() {
		return jsonValue;
	}

}
