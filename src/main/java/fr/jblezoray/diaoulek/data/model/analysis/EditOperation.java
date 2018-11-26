package fr.jblezoray.diaoulek.data.model.analysis;

public abstract class EditOperation<A> {

    public static class Delete<A> extends EditOperation<A> {
        public Delete(int index, A value) {
            super(index, value);
        }
        @Override
        public String toString() {
            return "Delete{" +
                    "index=" + this.index +
                    ", value=" + this.value +
                    '}';
        }
    }

    public static class Replace<A> extends EditOperation<A> {
        public Replace(int index, A value) {
            super(index, value);
        }

        @Override
        public String toString() {
            return "Replace{" +
                    "index=" + this.index +
                    ", value=" + this.value +
                    '}';
        }
    }

    public static class Insert<A> extends EditOperation<A> {
        public Insert(int index, A value) {
            super(index, value);
        }

        @Override
        public String toString() {
            return "Insert{" +
                    "index=" + this.index +
                    ", value=" + this.value +
                    '}';
        }
    }

    protected final int index;
    protected final A value;

    protected EditOperation(int index, A value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public A getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditOperation)) return false;

        EditOperation<?> that = (EditOperation<?>) o;

        if (index != that.index) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }




}
