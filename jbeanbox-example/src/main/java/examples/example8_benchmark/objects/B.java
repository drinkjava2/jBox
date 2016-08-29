package examples.example8_benchmark.objects;

import java.util.Objects;

public class B {
    private final C c;
 
    public B(C c) {
        this.c = c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        B b = (B) o;
        return Objects.equals(c, b.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(c);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
