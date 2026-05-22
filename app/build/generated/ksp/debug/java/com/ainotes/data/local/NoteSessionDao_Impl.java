package com.ainotes.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ainotes.data.model.NoteSession;
import com.ainotes.data.model.StudyNotes;
import com.ainotes.data.model.StudyNotesConverters;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class NoteSessionDao_Impl implements NoteSessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NoteSession> __insertionAdapterOfNoteSession;

  private final StudyNotesConverters __studyNotesConverters = new StudyNotesConverters();

  private final EntityDeletionOrUpdateAdapter<NoteSession> __deletionAdapterOfNoteSession;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSessionById;

  private final SharedSQLiteStatement __preparedStmtOfClearAllSessions;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSavedStatus;

  public NoteSessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNoteSession = new EntityInsertionAdapter<NoteSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `note_sessions` (`id`,`title`,`inputType`,`mode`,`customQuery`,`notes`,`pageCount`,`processingTimeMs`,`createdAt`,`isSaved`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NoteSession entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getInputType());
        statement.bindString(4, entity.getMode());
        statement.bindString(5, entity.getCustomQuery());
        final String _tmp = __studyNotesConverters.fromStudyNotes(entity.getNotes());
        statement.bindString(6, _tmp);
        statement.bindLong(7, entity.getPageCount());
        statement.bindLong(8, entity.getProcessingTimeMs());
        statement.bindLong(9, entity.getCreatedAt());
        final int _tmp_1 = entity.isSaved() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
      }
    };
    this.__deletionAdapterOfNoteSession = new EntityDeletionOrUpdateAdapter<NoteSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `note_sessions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NoteSession entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteSessionById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM note_sessions WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllSessions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM note_sessions";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSavedStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE note_sessions SET isSaved = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSession(final NoteSession session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNoteSession.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSession(final NoteSession session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfNoteSession.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSessionById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSessionById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteSessionById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllSessions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllSessions.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllSessions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSavedStatus(final String id, final boolean isSaved,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSavedStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isSaved ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateSavedStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<NoteSession>> getAllSessions() {
    final String _sql = "SELECT * FROM note_sessions ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"note_sessions"}, new Callable<List<NoteSession>>() {
      @Override
      @NonNull
      public List<NoteSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfInputType = CursorUtil.getColumnIndexOrThrow(_cursor, "inputType");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfCustomQuery = CursorUtil.getColumnIndexOrThrow(_cursor, "customQuery");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "pageCount");
          final int _cursorIndexOfProcessingTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "processingTimeMs");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final List<NoteSession> _result = new ArrayList<NoteSession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteSession _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpInputType;
            _tmpInputType = _cursor.getString(_cursorIndexOfInputType);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final String _tmpCustomQuery;
            _tmpCustomQuery = _cursor.getString(_cursorIndexOfCustomQuery);
            final StudyNotes _tmpNotes;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfNotes);
            _tmpNotes = __studyNotesConverters.toStudyNotes(_tmp);
            final int _tmpPageCount;
            _tmpPageCount = _cursor.getInt(_cursorIndexOfPageCount);
            final long _tmpProcessingTimeMs;
            _tmpProcessingTimeMs = _cursor.getLong(_cursorIndexOfProcessingTimeMs);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsSaved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp_1 != 0;
            _item = new NoteSession(_tmpId,_tmpTitle,_tmpInputType,_tmpMode,_tmpCustomQuery,_tmpNotes,_tmpPageCount,_tmpProcessingTimeMs,_tmpCreatedAt,_tmpIsSaved);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getSessionById(final String id,
      final Continuation<? super NoteSession> $completion) {
    final String _sql = "SELECT * FROM note_sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NoteSession>() {
      @Override
      @Nullable
      public NoteSession call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfInputType = CursorUtil.getColumnIndexOrThrow(_cursor, "inputType");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfCustomQuery = CursorUtil.getColumnIndexOrThrow(_cursor, "customQuery");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "pageCount");
          final int _cursorIndexOfProcessingTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "processingTimeMs");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final NoteSession _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpInputType;
            _tmpInputType = _cursor.getString(_cursorIndexOfInputType);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final String _tmpCustomQuery;
            _tmpCustomQuery = _cursor.getString(_cursorIndexOfCustomQuery);
            final StudyNotes _tmpNotes;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfNotes);
            _tmpNotes = __studyNotesConverters.toStudyNotes(_tmp);
            final int _tmpPageCount;
            _tmpPageCount = _cursor.getInt(_cursorIndexOfPageCount);
            final long _tmpProcessingTimeMs;
            _tmpProcessingTimeMs = _cursor.getLong(_cursorIndexOfProcessingTimeMs);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsSaved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp_1 != 0;
            _result = new NoteSession(_tmpId,_tmpTitle,_tmpInputType,_tmpMode,_tmpCustomQuery,_tmpNotes,_tmpPageCount,_tmpProcessingTimeMs,_tmpCreatedAt,_tmpIsSaved);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<NoteSession>> searchSessions(final String query) {
    final String _sql = "SELECT * FROM note_sessions WHERE title LIKE '%' || ? || '%' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"note_sessions"}, new Callable<List<NoteSession>>() {
      @Override
      @NonNull
      public List<NoteSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfInputType = CursorUtil.getColumnIndexOrThrow(_cursor, "inputType");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfCustomQuery = CursorUtil.getColumnIndexOrThrow(_cursor, "customQuery");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "pageCount");
          final int _cursorIndexOfProcessingTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "processingTimeMs");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final List<NoteSession> _result = new ArrayList<NoteSession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteSession _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpInputType;
            _tmpInputType = _cursor.getString(_cursorIndexOfInputType);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final String _tmpCustomQuery;
            _tmpCustomQuery = _cursor.getString(_cursorIndexOfCustomQuery);
            final StudyNotes _tmpNotes;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfNotes);
            _tmpNotes = __studyNotesConverters.toStudyNotes(_tmp);
            final int _tmpPageCount;
            _tmpPageCount = _cursor.getInt(_cursorIndexOfPageCount);
            final long _tmpProcessingTimeMs;
            _tmpProcessingTimeMs = _cursor.getLong(_cursorIndexOfProcessingTimeMs);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsSaved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp_1 != 0;
            _item = new NoteSession(_tmpId,_tmpTitle,_tmpInputType,_tmpMode,_tmpCustomQuery,_tmpNotes,_tmpPageCount,_tmpProcessingTimeMs,_tmpCreatedAt,_tmpIsSaved);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
