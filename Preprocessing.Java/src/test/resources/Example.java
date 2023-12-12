public class AstVisitorTest {
    public void good() {
        int i = 0;

        while (i == 1) {
            if (i == 2) {
                continue;
            } else {
                break;
            }
        }
    }
}