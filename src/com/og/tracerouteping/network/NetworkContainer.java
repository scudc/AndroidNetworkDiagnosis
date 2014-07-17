/*
This file is part of the project TraceroutePing, which is an Android library
implementing Traceroute with ping under GPL license v3.
Copyright (C) 2013  Olivier Goutay

TraceroutePing is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

TraceroutePing is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with TraceroutePing.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.og.tracerouteping.network;

import java.io.Serializable;

/**
 * 
 * @author Olivier Goutay
 * 
 */
public class NetworkContainer implements Serializable {

	private static final long serialVersionUID = 1034744411998219581L;

	private String cmdName;
	public String getCmdName() {
		return cmdName;
	}


	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}


	public String getCmdResult() {
		return cmdResult;
	}


	public void setCmdResult(String cmdResult) {
		this.cmdResult = cmdResult;
	}


	private String cmdResult;


	public NetworkContainer(String cmdName, String cmdResult) {
		this.cmdName = cmdName;
		this.cmdResult = cmdResult;
	}

	

}
