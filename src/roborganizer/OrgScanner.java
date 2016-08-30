package roborganizer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Standard java.util roborganizer.OrgScanner is buffered.
 * <p>
 * Created by robaut on 8/27/16.
 */
public class OrgScanner {
    InputStream innerStream;

    public OrgScanner(InputStream innerStream) {
        this.innerStream = innerStream;
    }

    public String nextLine() throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        int read = innerStream.read();
        while (read != -1 && read != '\n') {
            stringBuffer.append((char) read);
            read = innerStream.read();
        }
        return stringBuffer.toString();
    }

    public int nextInt() throws IOException {
        return Integer.parseInt(nextLine());
    }

    public boolean nextBoolean() throws IOException {
        return Boolean.parseBoolean(nextLine());
    }
}
