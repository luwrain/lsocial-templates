// NDA, Copyright Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.webdebug;

import java.util.*;

import lombok.*;

import static java.util.stream.Collectors.*;


@Data
@AllArgsConstructor
public class MainPageItem
{
    String type, name, updated;
}
