package fr.jblezoray.diaoulek.core.levenshtein;

public interface EditOperation<A> {

    class Delete<A> implements EditOperation<A> {
        public Delete(int i) {
            // TODO
        }
    }

    class Replace<A> implements EditOperation<A> {
        public Replace(int i, A replacementValue) {
            // TODO
        }
    }

    class Insert<A> implements EditOperation<A> {
        public Insert(int i, A insertValue) {
            // TODO
        }
    }

}
