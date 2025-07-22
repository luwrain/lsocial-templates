// NDA, Copyright Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.webdebug;

import java.util.*;

import org.apache.velocity.app.*;
import org.apache.velocity.*;
import org.apache.velocity.runtime.resource.loader.*;
import org.apache.velocity.runtime.*;
import org.apache.velocity.exception.*;
import org.apache.velocity.runtime.resource.util.*;

final class Engine
{
    final VelocityEngine engine;

    Engine(Map<String, String> templates) throws ResourceNotFoundException
    {
	engine = new VelocityEngine();
	Properties props = new Properties();
	props.setProperty("str.resource.loader.class", StringResourceLoader.class.getName());
	props.setProperty("str.resource.loader.cache", "true");
	props.setProperty(RuntimeConstants.RESOURCE_LOADER, "str");
	engine.init(props);
	final var  repo = StringResourceLoader.getRepository();
	for(var e: templates.entrySet())
	    repo.putStringResource(e.getKey(), e.getValue());
    }
}
