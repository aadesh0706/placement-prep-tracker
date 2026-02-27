import { useState, useEffect } from 'react';
import { adminAPI } from '../../services/api';
import {
  UsersIcon,
  BookOpenIcon,
  TrophyIcon,
  ChartBarIcon,
  TrashIcon,
  PencilIcon,
  PlusIcon,
  XMarkIcon,
  CheckCircleIcon,
  XCircleIcon,
} from '@heroicons/react/24/outline';

const TABS = [
  { key: 'stats', label: 'Overview' },
  { key: 'users', label: 'Users' },
  { key: 'quizzes', label: 'Quizzes' },
  { key: 'leaderboard', label: 'Leaderboard' },
];

const ROLES = ['STUDENT', 'TPO', 'ADMIN'];
const CATEGORIES = ['DSA', 'APTITUDE', 'OS', 'DBMS', 'CN', 'TECHNICAL', 'HR'];
const DIFFICULTIES = ['EASY', 'MEDIUM', 'HARD'];

// ─── Quiz form ────────────────────────────────────────────────────────────────
const emptyQuestion = () => ({ text: '', options: ['', '', '', ''], correctOptionIndex: 0, marks: 1 });
const emptyQuizForm = () => ({ title: '', description: '', category: 'DSA', difficulty: 'MEDIUM', timeLimit: 30, questions: [emptyQuestion()] });

const AdminDashboard = () => {
  const [tab, setTab] = useState('stats');
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [quizzes, setQuizzes] = useState([]);
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(false);

  // Quiz form modal
  const [showQuizForm, setShowQuizForm] = useState(false);
  const [quizForm, setQuizForm] = useState(emptyQuizForm());
  const [editingQuizId, setEditingQuizId] = useState(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => { loadTab(tab); }, [tab]);

  const loadTab = async (t) => {
    setLoading(true);
    try {
      if (t === 'stats' && !stats) setStats(await adminAPI.getStats());
      if (t === 'users') setUsers(await adminAPI.getUsers());
      if (t === 'quizzes') setQuizzes(await adminAPI.getQuizzes());
      if (t === 'leaderboard') setLeaderboard(await adminAPI.getLeaderboard());
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  };

  // ── User actions ──
  const updateRole = async (id, role) => {
    try { await adminAPI.updateUserRole(id, role); setUsers(u => u.map(x => x.id === id ? { ...x, role } : x)); }
    catch (e) { alert('Failed'); }
  };

  const toggleActive = async (id, active) => {
    try { await adminAPI.toggleUserActive(id); setUsers(u => u.map(x => x.id === id ? { ...x, active: !active } : x)); }
    catch (e) { alert('Failed'); }
  };

  const deleteUser = async (id) => {
    if (!confirm('Delete this user?')) return;
    try { await adminAPI.deleteUser(id); setUsers(u => u.filter(x => x.id !== id)); }
    catch (e) { alert('Failed'); }
  };

  // ── Quiz actions ──
  const openCreateQuiz = () => { setEditingQuizId(null); setQuizForm(emptyQuizForm()); setShowQuizForm(true); };
  const openEditQuiz = (q) => {
    setEditingQuizId(q.id);
    setQuizForm({
      title: q.title, description: q.description || '', category: q.category,
      difficulty: q.difficulty, timeLimit: q.timeLimit || 30,
      questions: q.questions?.length ? q.questions : [emptyQuestion()],
    });
    setShowQuizForm(true);
  };

  const saveQuiz = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      if (editingQuizId) { await adminAPI.updateQuiz(editingQuizId, quizForm); }
      else { await adminAPI.createQuiz(quizForm); }
      setShowQuizForm(false);
      setQuizzes(await adminAPI.getQuizzes());
    } catch (err) { alert('Failed to save quiz'); }
    finally { setSaving(false); }
  };

  const deleteQuiz = async (id) => {
    if (!confirm('Delete this quiz?')) return;
    try { await adminAPI.deleteQuiz(id); setQuizzes(q => q.filter(x => x.id !== id)); }
    catch (e) { alert('Failed'); }
  };

  const toggleQuizActive = async (id, active) => {
    try { await adminAPI.toggleQuizActive(id); setQuizzes(q => q.map(x => x.id === id ? { ...x, active: !active } : x)); }
    catch (e) { alert('Failed'); }
  };

  // ── Question helpers ──
  const updateQuestion = (qi, field, value) => {
    const qs = [...quizForm.questions];
    qs[qi] = { ...qs[qi], [field]: value };
    setQuizForm({ ...quizForm, questions: qs });
  };
  const updateOption = (qi, oi, value) => {
    const qs = [...quizForm.questions];
    const opts = [...qs[qi].options]; opts[oi] = value;
    qs[qi] = { ...qs[qi], options: opts };
    setQuizForm({ ...quizForm, questions: qs });
  };
  const addQuestion = () => setQuizForm({ ...quizForm, questions: [...quizForm.questions, emptyQuestion()] });
  const removeQuestion = (qi) => setQuizForm({ ...quizForm, questions: quizForm.questions.filter((_, i) => i !== qi) });

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Admin Panel</h1>

      {/* Tabs */}
      <div className="flex space-x-1 bg-gray-100 rounded-xl p-1 w-fit">
        {TABS.map(t => (
          <button key={t.key} onClick={() => setTab(t.key)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${tab === t.key ? 'bg-white text-gray-900 shadow' : 'text-gray-600 hover:text-gray-900'}`}>
            {t.label}
          </button>
        ))}
      </div>

      {loading && <div className="flex justify-center p-12"><div className="animate-spin rounded-full h-10 w-10 border-t-2 border-primary-600" /></div>}

      {/* ── Stats ── */}
      {!loading && tab === 'stats' && stats && (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <KpiCard icon={UsersIcon} color="bg-blue-50 text-blue-600" label="Total Users" value={stats.totalUsers ?? 0} />
          <KpiCard icon={BookOpenIcon} color="bg-green-50 text-green-600" label="Total Quizzes" value={stats.totalQuizzes ?? 0} />
          <KpiCard icon={ChartBarIcon} color="bg-purple-50 text-purple-600" label="Quiz Attempts" value={stats.totalQuizAttempts ?? 0} />
          <KpiCard icon={TrophyIcon} color="bg-yellow-50 text-yellow-600" label="Mock Interviews" value={stats.totalMockInterviews ?? 0} />
        </div>
      )}

      {/* ── Users ── */}
      {!loading && tab === 'users' && (
        <div className="card overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-gray-500">
                <th className="pb-2 pr-4 font-medium">Name</th>
                <th className="pb-2 pr-4 font-medium">Email</th>
                <th className="pb-2 pr-4 font-medium">Role</th>
                <th className="pb-2 pr-4 font-medium">Status</th>
                <th className="pb-2 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(u => (
                <tr key={u.id} className="border-b last:border-0 hover:bg-gray-50">
                  <td className="py-3 pr-4 font-medium text-gray-900">{u.name}</td>
                  <td className="py-3 pr-4 text-gray-500">{u.email}</td>
                  <td className="py-3 pr-4">
                    <select value={u.role} onChange={e => updateRole(u.id, e.target.value)}
                      className="text-xs border rounded px-2 py-1">
                      {ROLES.map(r => <option key={r}>{r}</option>)}
                    </select>
                  </td>
                  <td className="py-3 pr-4">
                    <span className={`text-xs px-2 py-1 rounded-full font-medium ${u.active !== false ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                      {u.active !== false ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td className="py-3 flex items-center space-x-2">
                    <button onClick={() => toggleActive(u.id, u.active !== false)} title={u.active !== false ? 'Deactivate' : 'Activate'}
                      className="text-gray-400 hover:text-yellow-500 transition-colors">
                      {u.active !== false ? <XCircleIcon className="w-5 h-5" /> : <CheckCircleIcon className="w-5 h-5" />}
                    </button>
                    <button onClick={() => deleteUser(u.id)} className="text-gray-400 hover:text-red-500 transition-colors">
                      <TrashIcon className="w-5 h-5" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {users.length === 0 && <p className="text-center text-gray-400 py-8">No users found.</p>}
        </div>
      )}

      {/* ── Quizzes ── */}
      {!loading && tab === 'quizzes' && (
        <>
          <div className="flex justify-end">
            <button onClick={openCreateQuiz} className="btn btn-primary flex items-center space-x-1">
              <PlusIcon className="w-4 h-4" /><span>Create Quiz</span>
            </button>
          </div>
          <div className="space-y-3">
            {quizzes.length === 0 && <p className="card text-center text-gray-400 py-8">No quizzes yet.</p>}
            {quizzes.map(q => (
              <div key={q.id} className="card flex items-center justify-between">
                <div>
                  <div className="flex items-center space-x-2">
                    <p className="font-medium text-gray-900">{q.title}</p>
                    <span className={`text-xs px-2 py-0.5 rounded-full ${q.active !== false ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}>{q.active !== false ? 'Active' : 'Hidden'}</span>
                  </div>
                  <p className="text-xs text-gray-500">{q.category} · {q.difficulty} · {q.questions?.length || 0} questions · {q.totalMarks} marks</p>
                </div>
                <div className="flex items-center space-x-2">
                  <button onClick={() => toggleQuizActive(q.id, q.active !== false)} className="text-xs text-gray-500 hover:text-primary-600 border rounded px-2 py-1 transition-colors">
                    {q.active !== false ? 'Hide' : 'Show'}
                  </button>
                  <button onClick={() => openEditQuiz(q)} className="text-gray-400 hover:text-primary-500"><PencilIcon className="w-4 h-4" /></button>
                  <button onClick={() => deleteQuiz(q.id)} className="text-gray-400 hover:text-red-500"><TrashIcon className="w-4 h-4" /></button>
                </div>
              </div>
            ))}
          </div>
        </>
      )}

      {/* ── Leaderboard ── */}
      {!loading && tab === 'leaderboard' && (
        <div className="card overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-gray-500">
                <th className="pb-2 pr-4 font-medium">Rank</th>
                <th className="pb-2 pr-4 font-medium">Name</th>
                <th className="pb-2 pr-4 font-medium">Overall Progress</th>
                <th className="pb-2 pr-4 font-medium">Quiz Score</th>
                <th className="pb-2 font-medium">Streak</th>
              </tr>
            </thead>
            <tbody>
              {leaderboard.map((u, i) => (
                <tr key={u.userId || i} className="border-b last:border-0 hover:bg-gray-50">
                  <td className="py-3 pr-4">
                    <span className={`font-bold ${i === 0 ? 'text-yellow-500' : i === 1 ? 'text-gray-400' : i === 2 ? 'text-orange-400' : 'text-gray-500'}`}>{i + 1}</span>
                  </td>
                  <td className="py-3 pr-4 font-medium text-gray-900">{u.userName || u.name}</td>
                  <td className="py-3 pr-4">
                    <div className="flex items-center space-x-2">
                      <div className="w-24 bg-gray-200 rounded-full h-1.5">
                        <div className="bg-primary-500 h-1.5 rounded-full" style={{ width: `${Math.round(u.overallProgress || 0)}%` }} />
                      </div>
                      <span className="text-xs text-gray-500">{Math.round(u.overallProgress || 0)}%</span>
                    </div>
                  </td>
                  <td className="py-3 pr-4 text-gray-700">{Math.round(u.averageQuizScore || 0)}%</td>
                  <td className="py-3 text-gray-700 flex items-center"><span className="mr-1">🔥</span>{u.currentStreak || 0}d</td>
                </tr>
              ))}
            </tbody>
          </table>
          {leaderboard.length === 0 && <p className="text-center text-gray-400 py-8">No data yet.</p>}
        </div>
      )}

      {/* ── Quiz Form Modal ── */}
      {showQuizForm && (
        <div className="fixed inset-0 bg-black/50 z-50 overflow-y-auto">
          <div className="min-h-screen flex items-start justify-center p-4 py-8">
            <div className="bg-white rounded-2xl shadow-xl w-full max-w-2xl">
              <div className="flex items-center justify-between p-6 border-b">
                <h2 className="text-lg font-semibold">{editingQuizId ? 'Edit Quiz' : 'Create Quiz'}</h2>
                <button onClick={() => setShowQuizForm(false)}><XMarkIcon className="w-5 h-5 text-gray-500" /></button>
              </div>
              <form onSubmit={saveQuiz} className="p-6 space-y-5">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium mb-1">Title</label>
                    <input type="text" value={quizForm.title} onChange={e => setQuizForm({ ...quizForm, title: e.target.value })} className="input" required />
                  </div>
                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium mb-1">Description</label>
                    <input type="text" value={quizForm.description} onChange={e => setQuizForm({ ...quizForm, description: e.target.value })} className="input" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Category</label>
                    <select value={quizForm.category} onChange={e => setQuizForm({ ...quizForm, category: e.target.value })} className="input">
                      {CATEGORIES.map(c => <option key={c}>{c}</option>)}
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Difficulty</label>
                    <select value={quizForm.difficulty} onChange={e => setQuizForm({ ...quizForm, difficulty: e.target.value })} className="input">
                      {DIFFICULTIES.map(d => <option key={d}>{d}</option>)}
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Time Limit (min)</label>
                    <input type="number" min="1" value={quizForm.timeLimit} onChange={e => setQuizForm({ ...quizForm, timeLimit: +e.target.value })} className="input" required />
                  </div>
                </div>

                {/* Questions */}
                <div>
                  <div className="flex items-center justify-between mb-3">
                    <label className="text-sm font-medium">Questions</label>
                    <button type="button" onClick={addQuestion} className="text-xs text-primary-600 hover:text-primary-700 flex items-center"><PlusIcon className="w-3 h-3 mr-1" />Add Question</button>
                  </div>
                  <div className="space-y-4">
                    {quizForm.questions.map((q, qi) => (
                      <div key={qi} className="border border-gray-200 rounded-xl p-4">
                        <div className="flex items-center justify-between mb-3">
                          <span className="text-sm font-medium text-gray-700">Q{qi + 1}</span>
                          {quizForm.questions.length > 1 && (
                            <button type="button" onClick={() => removeQuestion(qi)} className="text-gray-400 hover:text-red-500"><XMarkIcon className="w-4 h-4" /></button>
                          )}
                        </div>
                        <input type="text" placeholder="Question text" value={q.text}
                          onChange={e => updateQuestion(qi, 'text', e.target.value)}
                          className="input mb-3" required />
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-2 mb-3">
                          {q.options.map((opt, oi) => (
                            <div key={oi} className="flex items-center space-x-2">
                              <input type="radio" name={`correct-${qi}`} checked={q.correctOptionIndex === oi}
                                onChange={() => updateQuestion(qi, 'correctOptionIndex', oi)}
                                className="text-primary-600" title="Mark as correct" />
                              <input type="text" placeholder={`Option ${String.fromCharCode(65 + oi)}`} value={opt}
                                onChange={e => updateOption(qi, oi, e.target.value)}
                                className="input text-sm flex-1" required />
                            </div>
                          ))}
                        </div>
                        <div className="flex items-center space-x-2">
                          <label className="text-xs text-gray-500">Marks:</label>
                          <input type="number" min="1" value={q.marks}
                            onChange={e => updateQuestion(qi, 'marks', +e.target.value)}
                            className="input w-20 text-sm" />
                          <span className="text-xs text-gray-400">(select radio for correct answer)</span>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="flex space-x-3 pt-2">
                  <button type="button" onClick={() => setShowQuizForm(false)} className="btn btn-secondary flex-1">Cancel</button>
                  <button type="submit" disabled={saving} className="btn btn-primary flex-1">
                    {saving ? 'Saving…' : editingQuizId ? 'Update Quiz' : 'Create Quiz'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const KpiCard = ({ icon: Icon, color, label, value }) => (
  <div className="card flex items-center space-x-4">
    <div className={`p-3 rounded-xl ${color}`}><Icon className="w-6 h-6" /></div>
    <div>
      <p className="text-2xl font-bold text-gray-900">{value}</p>
      <p className="text-xs text-gray-500">{label}</p>
    </div>
  </div>
);

export default AdminDashboard;
