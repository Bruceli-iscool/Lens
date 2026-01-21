package extensions;

import dev.desktop.LensExtension;
import dev.desktop.photoLibrary;

public class test implements LensExtension {
    @Override
    public void run(photoLibrary pl) {
        System.out.println("test");
    }
}
