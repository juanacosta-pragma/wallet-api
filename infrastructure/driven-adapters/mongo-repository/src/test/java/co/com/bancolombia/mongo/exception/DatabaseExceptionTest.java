package co.com.bancolombia.mongo.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseExceptionTest {

    @Test
    void testDatabaseExceptionCreation() {
        DatabaseException exception = new DatabaseException("Connection failed");

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Connection failed");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testDatabaseExceptionWithNull() {
        DatabaseException exception = new DatabaseException(null);

        assertThat(exception.getMessage()).isNull();
    }

    @Test
    void testDatabaseExceptionWithEmptyMessage() {
        DatabaseException exception = new DatabaseException("");

        assertThat(exception.getMessage()).isEmpty();
    }

    @Test
    void testDatabaseExceptionWithComplexMessage() {
        String complexMsg = "MongoDB Connection Error: Timeout after 5000ms waiting for server selection";
        DatabaseException exception = new DatabaseException(complexMsg);

        assertThat(exception.getMessage()).isEqualTo(complexMsg);
    }

    @Test
    void testDatabaseExceptionCausedBy() {
        RuntimeException cause = new RuntimeException("Original error");
        DatabaseException exception = new DatabaseException("Wrapper error");

        assertThat(exception).hasMessage("Wrapper error");
    }

    @Test
    void testMultipleDatabaseExceptions() {
        DatabaseException[] exceptions = {
            new DatabaseException("Error 1"),
            new DatabaseException("Error 2"),
            new DatabaseException("Error 3")
        };

        for (DatabaseException ex : exceptions) {
            assertThat(ex).isInstanceOf(RuntimeException.class);
        }
    }
}

