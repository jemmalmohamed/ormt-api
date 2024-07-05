package ma.org.ormt.core.commun.rest.responses;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MessageResponse {

    String title;

    String mainMessage;

    String subMessage;

    List<String> subMessageList;

    public String format() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("[title]").append(title).append("[/title]");
        stringBuilder.append("[mainMessage]").append(mainMessage).append("[/mainMessage]");
        if (subMessage != null)
            stringBuilder.append("[subMessage]").append(subMessage).append("[/subMessage]");

        if (subMessageList == null || subMessageList.isEmpty()) {
            return stringBuilder.toString();
        }
        String subMessageItems = subMessageList.stream()
                .map(subMessageItem -> "[subMessageList]" + subMessageItem + "[/subMessageList]")
                .collect(Collectors.joining());

        stringBuilder.append(subMessageItems);

        return stringBuilder.toString();
    }
}
