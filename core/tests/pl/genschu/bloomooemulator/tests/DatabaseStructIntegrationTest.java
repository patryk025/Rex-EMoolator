package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.interpreter.variable.db.DatabaseState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Database and Struct variables.
 * Tests the flow: DTA file -> DatabaseVariable -> StructVariable
 */
class DatabaseStructIntegrationTest {
    private Context ctx;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
    }

    @Test
    void testStructCreation() {
        List<String> fields = List.of("NAME", "AGE", "SCORE");
        List<String> types = List.of("STRING", "INTEGER", "DOUBLE");

        StructVariable struct = StructVariable.withSchema("PLAYER", fields, types);

        assertEquals("PLAYER", struct.name());
        assertEquals(3, struct.fields().size());
        assertEquals("NAME", struct.fields().get(0));
        assertEquals("AGE", struct.fields().get(1));
        assertEquals("SCORE", struct.fields().get(2));
        assertEquals("STRING", struct.types().get(0));
        assertEquals("INTEGER", struct.types().get(1));
        assertEquals("DOUBLE", struct.types().get(2));
    }

    @Test
    void testStructWithValues() {
        List<String> fields = List.of("NAME", "AGE");
        List<String> types = List.of("STRING", "INTEGER");
        List<Value> values = List.of(new StringValue("John"), new IntValue(25));

        StructVariable struct = new StructVariable("PERSON", fields, types, values);

        assertEquals("John", struct.getFieldByIndex(0).toDisplayString());
        assertEquals(25, struct.getFieldByIndex(1).toInt().value());
        assertEquals("John", struct.getFieldByName("NAME").toDisplayString());
        assertEquals(25, struct.getFieldByName("AGE").toInt().value());
    }

    @Test
    void testStructGetFieldIndex() {
        List<String> fields = List.of("A", "B", "C");
        List<String> types = List.of("STRING", "STRING", "STRING");

        StructVariable struct = StructVariable.withSchema("TEST", fields, types);

        assertEquals(0, struct.getFieldIndex("A"));
        assertEquals(1, struct.getFieldIndex("B"));
        assertEquals(2, struct.getFieldIndex("C"));
        assertEquals(-1, struct.getFieldIndex("NONEXISTENT"));
        assertEquals(1, struct.getFieldIndex("b")); // case-insensitive
    }

    @Test
    void testStructMethods() {
        List<String> fields = List.of("X", "Y");
        List<String> types = List.of("INTEGER", "INTEGER");
        List<Value> values = List.of(new IntValue(10), new IntValue(20));

        StructVariable struct = new StructVariable("COORDS", fields, types, values);

        // GETFIELD
        MethodResult result = struct.callMethod("GETFIELD", List.of(new IntValue(0)));
        assertEquals(10, result.getReturnValue().toInt().value());

        result = struct.callMethod("GETFIELD", List.of(new IntValue(1)));
        assertEquals(20, result.getReturnValue().toInt().value());
    }

    @Test
    void testDatabaseCreation() {
        DatabaseVariable db = new DatabaseVariable("SCORES");

        assertEquals("SCORES", db.name());
        assertEquals(VariableType.DATABASE, db.type());
        assertNotNull(db.state());
        assertEquals(0, db.state().rowsNo());
    }

    @Test
    void testDatabaseWithData() {
        DatabaseState state = new DatabaseState();
        state.setColumns(List.of("NAME", "SCORE"));
        state.setData(List.of(
            List.of("Alice", "100"),
            List.of("Bob", "85"),
            List.of("Charlie", "92")
        ));

        DatabaseVariable db = new DatabaseVariable("HIGHSCORES", state);

        assertEquals(3, db.state().rowsNo());
        assertEquals(List.of("Alice", "100"), db.state().currentRow());
    }

    @Test
    void testDatabaseMethods() {
        DatabaseState state = new DatabaseState();
        state.setColumns(List.of("NAME", "POINTS"));
        state.setData(List.of(
            List.of("Player1", "50"),
            List.of("Player2", "75"),
            List.of("Player3", "60")
        ));

        DatabaseVariable db = new DatabaseVariable("SCORES", state);
        ctx.setVariable("SCORES", db);

        // GETROWSNO
        MethodResult result = db.callMethod("GETROWSNO", List.of());
        assertEquals(3, result.getReturnValue().toInt().value());

        // NEXT
        assertEquals(0, db.state().currentRowIndex());
        db.callMethod("NEXT", List.of());
        assertEquals(1, db.state().currentRowIndex());
        assertEquals(List.of("Player2", "75"), db.state().currentRow());

        // SELECT
        db.callMethod("SELECT", List.of(new IntValue(2)));
        assertEquals(2, db.state().currentRowIndex());
        assertEquals(List.of("Player3", "60"), db.state().currentRow());

        // FIND
        result = db.callMethod("FIND", List.of(
            new StringValue("NAME"),
            new StringValue("Player2"),
            new IntValue(-1)
        ));
        assertEquals(1, result.getReturnValue().toInt().value());

        // FIND with default when not found
        result = db.callMethod("FIND", List.of(
            new StringValue("NAME"),
            new StringValue("NonExistent"),
            new IntValue(-1)
        ));
        assertEquals(-1, result.getReturnValue().toInt().value());
    }

    @Test
    void testDatabaseCursor() {
        DatabaseVariable db = new DatabaseVariable("MYDB");
        Variable cursor = db.getCursor();

        assertNotNull(cursor);
        assertTrue(cursor instanceof DatabaseCursorVariable);
        assertEquals("MYDB_CURSOR", cursor.name());
        assertEquals(VariableType.DATABASE_CURSOR, cursor.type());
    }

    @Test
    void testDatabaseRemoveAll() {
        DatabaseState state = new DatabaseState();
        state.setColumns(List.of("COL1"));
        state.setData(List.of(
            List.of("A"),
            List.of("B")
        ));

        DatabaseVariable db = new DatabaseVariable("TEST", state);
        assertEquals(2, db.state().rowsNo());

        db.callMethod("REMOVEALL", List.of());
        assertEquals(0, db.state().rowsNo());
    }

    @Test
    void testStructToRowStrings() {
        List<String> fields = List.of("NAME", "AGE", "ACTIVE");
        List<String> types = List.of("STRING", "INTEGER", "BOOLEAN");
        List<Value> values = List.of(
            new StringValue("Test"),
            new IntValue(42),
            new BoolValue(true)
        );

        StructVariable struct = new StructVariable("RECORD", fields, types, values);
        List<String> rowStrings = struct.toRowStrings();

        assertEquals(3, rowStrings.size());
        assertEquals("Test", rowStrings.get(0));
        assertEquals("42", rowStrings.get(1));
        assertEquals("TRUE", rowStrings.get(2)); // BoolValue uses uppercase
    }

    @Test
    void testCursorSetFromStruct() {
        // Setup database with data
        DatabaseState state = new DatabaseState();
        state.setColumns(List.of("NAME", "VALUE"));
        state.setData(List.of(
            List.of("Original", "100")
        ));

        DatabaseVariable db = new DatabaseVariable("DATA", state);
        DatabaseCursorVariable cursor = new DatabaseCursorVariable(db);

        // Create struct with new values
        List<String> fields = List.of("NAME", "VALUE");
        List<String> types = List.of("STRING", "INTEGER");
        List<Value> values = List.of(new StringValue("Updated"), new IntValue(200));
        StructVariable struct = new StructVariable("MODEL", fields, types, values);

        // Update database row via cursor
        cursor.setFromStruct(struct);

        // Verify database was updated
        assertEquals("Updated", db.state().currentRow().get(0));
        assertEquals("200", db.state().currentRow().get(1));
    }

    @Test
    void testStructWithValueAt() {
        List<String> fields = List.of("A", "B");
        List<String> types = List.of("INTEGER", "INTEGER");
        List<Value> values = List.of(new IntValue(1), new IntValue(2));

        StructVariable struct = new StructVariable("TEST", fields, types, values);
        StructVariable updated = struct.withValueAt(0, new IntValue(100));

        assertSame(struct, updated);
        // Value is updated
        assertEquals(100, struct.getFieldByIndex(0).toInt().value());
        assertEquals(2, struct.getFieldByIndex(1).toInt().value());
    }

    @Test
    void testDatabaseNextWrapsAround() {
        DatabaseState state = new DatabaseState();
        state.setColumns(List.of("ID"));
        state.setData(List.of(
            List.of("1"),
            List.of("2"),
            List.of("3")
        ));

        DatabaseVariable db = new DatabaseVariable("TEST", state);

        assertEquals(0, db.state().currentRowIndex());
        db.callMethod("NEXT", List.of());
        assertEquals(1, db.state().currentRowIndex());
        db.callMethod("NEXT", List.of());
        assertEquals(2, db.state().currentRowIndex());
        db.callMethod("NEXT", List.of());
        assertEquals(0, db.state().currentRowIndex()); // Wraps around
    }

    @Test
    void testFindCaseInsensitive() {
        DatabaseState state = new DatabaseState();
        state.setColumns(List.of("NAME"));
        state.setData(List.of(
            List.of("Alice"),
            List.of("BOB"),
            List.of("charlie")
        ));

        DatabaseVariable db = new DatabaseVariable("TEST", state);

        // Find should be case-insensitive
        MethodResult result = db.callMethod("FIND", List.of(
            new StringValue("name"),  // lowercase column name
            new StringValue("bob"),   // lowercase search value
            new IntValue(-1)
        ));
        assertEquals(1, result.getReturnValue().toInt().value());
    }

    @Test
    void testDatabaseInitWithModel() {
        // Create STRUCT with schema
        List<String> fields = List.of("PLAYER", "SCORE", "LEVEL");
        List<String> types = List.of("STRING", "INTEGER", "INTEGER");
        StructVariable struct = StructVariable.withSchema("HIGHSCORE_MODEL", fields, types);

        // Create DATABASE with MODEL reference
        DatabaseVariable db = new DatabaseVariable("HIGHSCORES", "HIGHSCORE_MODEL", new DatabaseState());

        // Add both to context
        ctx.setVariable("HIGHSCORE_MODEL", struct);
        ctx.setVariable("HIGHSCORES", db);

        // Verify columns are empty before init
        assertTrue(db.state().columns().isEmpty());

        // Call init
        db.init(ctx);

        // Verify columns were copied from STRUCT
        assertEquals(3, db.state().columns().size());
        assertEquals("PLAYER", db.state().columns().get(0));
        assertEquals("SCORE", db.state().columns().get(1));
        assertEquals("LEVEL", db.state().columns().get(2));
    }

    @Test
    void testDatabaseInitWithoutModel() {
        // DATABASE without MODEL should not fail
        DatabaseVariable db = new DatabaseVariable("MYDB");
        db.init(ctx);  // Should not throw

        assertTrue(db.state().columns().isEmpty());
    }

    @Test
    void testDatabaseModelName() {
        DatabaseVariable db = new DatabaseVariable("SCORES", "SCORE_STRUCT", new DatabaseState());

        assertEquals("SCORES", db.name());
        assertEquals("SCORE_STRUCT", db.modelName());
    }
}
