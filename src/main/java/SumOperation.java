import java.util.function.BinaryOperator;

public class SumOperation implements BinaryOperator<Integer> {

    @Override
    public Integer apply(Integer f, Integer g) {
        return f + g;
    }
}
