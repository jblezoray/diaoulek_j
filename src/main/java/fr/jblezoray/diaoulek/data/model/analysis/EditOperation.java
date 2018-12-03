package fr.jblezoray.diaoulek.data.model.analysis;

public abstract class EditOperation<A> {

    public static class Delete<A> extends EditOperation<A> {
        public Delete(int index, A fromValue, A toValue) {
            super(index, fromValue, toValue);
        }

        @Override
        public String toString() {
            return "Delete{" +
                    "index=" + this.index +
                    ", fromValue=" + this.fromValue +
                    ", toValue=" + this.toValue +
                    '}';
        }
    }

    public static class Equality<A> extends EditOperation<A> {
        public Equality(int index, A fromValue, A toValue) {
            super(index, fromValue, toValue);
        }

        @Override
        public String toString() {
            return "Equality{" +
                    "index=" + this.index +
                    ", fromValue=" + this.fromValue +
                    ", toValue=" + this.toValue +
                    '}';
        }
    }

    public static class Replace<A> extends EditOperation<A> {
        public Replace(int index, A fromValue, A toValue) {
            super(index, fromValue, toValue);
        }

        @Override
        public String toString() {
            return "Replace{" +
                    "index=" + this.index +
                    ", fromValue=" + this.fromValue +
                    ", toValue=" + this.toValue +
                    '}';
        }
    }

    public static class Insert<A> extends EditOperation<A> {
        public Insert(int index, A fromValue, A toValue) {
            super(index, fromValue, toValue);
        }

        @Override
        public String toString() {
            return "Insert{" +
                    "index=" + this.index +
                    ", fromValue=" + this.fromValue +
                    ", toValue=" + this.toValue +
                    '}';
        }
    }

    protected final int index;
    protected final A fromValue;
    protected final A toValue;

    protected EditOperation(int index, A fromValue, A toValue) {
        this.index = index;
        this.fromValue = fromValue;
        this.toValue = toValue;
    }

    public int getIndex() {
        return index;
    }

    public A getFromValue() {
        return fromValue;
    }

    public A getToValue() {
        return toValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditOperation)) return false;

        EditOperation<?> that = (EditOperation<?>) o;

        if (index != that.index) return false;
        if (fromValue != null ? !fromValue.equals(that.fromValue) : that.fromValue != null) return false;
        return toValue != null ? toValue.equals(that.toValue) : that.toValue == null;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (fromValue != null ? fromValue.hashCode() : 0);
        result = 31 * result + (toValue != null ? toValue.hashCode() : 0);
        return result;
    }
}
