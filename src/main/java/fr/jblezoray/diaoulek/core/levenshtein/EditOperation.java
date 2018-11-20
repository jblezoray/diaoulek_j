package fr.jblezoray.diaoulek.core.levenshtein;

public interface EditOperation<A> {

    class Delete<A> implements EditOperation<A> {
        private final int index;
        public Delete(int index) {
            this.index = index;
        }
        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return "Delete{" +
                    "index=" + index +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Delete<?> delete = (Delete<?>) o;
            return index == delete.index;
        }
    }

    class Replace<A> implements EditOperation<A> {
        private final int index;
        private final A replacementValue;
        public Replace(int index, A replacementValue) {
            this.index = index;
            this.replacementValue = replacementValue;
        }
        public int getIndex() {
            return index;
        }
        public A getReplacementValue() {
            return replacementValue;
        }

        @Override
        public String toString() {
            return "Replace{" +
                    "index=" + index +
                    ", replacementValue=" + replacementValue +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Replace<?> replace = (Replace<?>) o;
            if (index != replace.index) return false;
            return replacementValue.equals(replace.replacementValue);
        }
    }

    class Insert<A> implements EditOperation<A> {
        private final int index;
        private final A insertValue;
        public Insert(int index, A insertValue) {
            this.index = index;
            this.insertValue = insertValue;
        }
        public int getIndex() {
            return index;
        }
        public A getInsertValue() {
            return insertValue;
        }

        @Override
        public String toString() {
            return "Insert{" +
                    "index=" + index +
                    ", insertValue=" + insertValue +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Insert<?> insert = (Insert<?>) o;
            if (index != insert.index) return false;
            return insertValue.equals(insert.insertValue);
        }
    }

}
