import { useState, useEffect } from 'react';
import { interviewAPI } from '../services/api';
import {
  VideoCameraIcon,
  ChatBubbleLeftRightIcon,
  PlusIcon,
  StarIcon,
  ChevronDownIcon,
  ChevronUpIcon,
  XMarkIcon,
} from '@heroicons/react/24/outline';

const TYPES = ['TECHNICAL', 'HR', 'SYSTEM_DESIGN'];
const DIFFICULTIES = ['EASY', 'MEDIUM', 'HARD'];

const typeIcon = { TECHNICAL: VideoCameraIcon, HR: ChatBubbleLeftRightIcon, SYSTEM_DESIGN: VideoCameraIcon };
const typeColor = { TECHNICAL: 'bg-blue-100 text-blue-700', HR: 'bg-purple-100 text-purple-700', SYSTEM_DESIGN: 'bg-orange-100 text-orange-700' };

const MockInterviews = () => {
  const [interviews, setInterviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [expanded, setExpanded] = useState(null);
  const [creating, setCreating] = useState(false);
  const [activeInterview, setActiveInterview] = useState(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [responseText, setResponseText] = useState('');
  const [submittingResponse, setSubmittingResponse] = useState(false);
  const [form, setForm] = useState({ type: 'TECHNICAL', difficulty: 'MEDIUM', topics: '' });

  useEffect(() => { fetchInterviews(); }, []);

  const fetchInterviews = async () => {
    setLoading(true);
    try { setInterviews(await interviewAPI.getAll()); }
    catch (e) { console.error(e); }
    finally { setLoading(false); }
  };

  const startInterviewSession = async (interview) => {
    try {
      const started = await interviewAPI.start(interview.id);
      setActiveInterview({ ...interview, ...started, status: 'IN_PROGRESS' });
      setCurrentQuestionIndex(0);
      setResponseText('');
    } catch (e) { alert('Failed to start interview'); }
  };

  const submitResponse = async () => {
    if (!responseText.trim()) return;
    setSubmittingResponse(true);
    try {
      const q = activeInterview.questions[currentQuestionIndex];
      await interviewAPI.respond(activeInterview.id, q.id, responseText);
      
      if (currentQuestionIndex < activeInterview.questions.length - 1) {
        setCurrentQuestionIndex(currentQuestionIndex + 1);
        setResponseText('');
      } else {
        // All questions answered - complete interview
        await interviewAPI.complete(activeInterview.id, { feedback: 'Interview completed' });
        fetchInterviews();
        setActiveInterview(null);
      }
    } catch (e) { alert('Failed to submit response'); }
    finally { setSubmittingResponse(false); }
  };

  const createInterview = async (e) => {
    e.preventDefault();
    setCreating(true);
    try {
      await interviewAPI.create({
        type: form.type,
        difficulty: form.difficulty,
        topics: form.topics.split(',').map(s => s.trim()).filter(Boolean),
      });
      setShowCreate(false);
      setForm({ type: 'TECHNICAL', difficulty: 'MEDIUM', topics: '' });
      fetchInterviews();
    } catch (err) { alert('Failed to create interview'); }
    finally { setCreating(false); }
  };

  const scoreColor = (s) => s >= 8 ? 'text-green-600' : s >= 5 ? 'text-yellow-600' : 'text-red-500';

  if (loading) return <div className="flex justify-center p-12"><div className="animate-spin rounded-full h-10 w-10 border-t-2 border-primary-600" /></div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Mock Interviews</h1>
        <button onClick={() => setShowCreate(true)} className="btn btn-primary flex items-center space-x-1">
          <PlusIcon className="w-4 h-4" /><span>New Interview</span>
        </button>
      </div>

      {showCreate && (
        <div className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md">
            <div className="flex items-center justify-between p-6 border-b">
              <h2 className="text-lg font-semibold">Start Mock Interview</h2>
              <button onClick={() => setShowCreate(false)}><XMarkIcon className="w-5 h-5 text-gray-500" /></button>
            </div>
            <form onSubmit={createInterview} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Interview Type</label>
                <div className="grid grid-cols-3 gap-2">
                  {TYPES.map(t => (
                    <button type="button" key={t} onClick={() => setForm({ ...form, type: t })}
                      className={`px-3 py-2 rounded-lg text-sm font-medium border-2 transition-colors ${form.type === t ? 'border-primary-500 bg-primary-50 text-primary-700' : 'border-gray-200 text-gray-600 hover:border-gray-300'}`}>
                      {t.replace('_', ' ')}
                    </button>
                  ))}
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Difficulty</label>
                <div className="grid grid-cols-3 gap-2">
                  {DIFFICULTIES.map(d => (
                    <button type="button" key={d} onClick={() => setForm({ ...form, difficulty: d })}
                      className={`px-3 py-2 rounded-lg text-sm font-medium border-2 transition-colors ${form.difficulty === d ? 'border-primary-500 bg-primary-50 text-primary-700' : 'border-gray-200 text-gray-600 hover:border-gray-300'}`}>
                      {d}
                    </button>
                  ))}
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Topics (comma-separated)</label>
                <input type="text" value={form.topics} onChange={e => setForm({ ...form, topics: e.target.value })}
                  className="input" placeholder="e.g. Arrays, OOP, System Design" />
              </div>
              <div className="flex space-x-3 pt-2">
                <button type="button" onClick={() => setShowCreate(false)} className="btn btn-secondary flex-1">Cancel</button>
                <button type="submit" disabled={creating} className="btn btn-primary flex-1">
                  {creating ? 'Starting…' : 'Start Interview'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {interviews.length === 0 ? (
        <div className="card text-center py-16">
          <VideoCameraIcon className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-gray-700 mb-2">No interviews yet</h3>
          <p className="text-gray-500 mb-6">Start a mock interview to practice and get AI feedback.</p>
          <button onClick={() => setShowCreate(true)} className="btn btn-primary mx-auto">Start Your First Interview</button>
        </div>
      ) : (
        <div className="space-y-3">
          {interviews.map(iv => {
            const Icon = typeIcon[iv.type] || VideoCameraIcon;
            const isExp = expanded === iv.id;
            const score = iv.overallScore;
            return (
              <div key={iv.id} className="card">
                <div className="flex items-center justify-between cursor-pointer" onClick={() => setExpanded(isExp ? null : iv.id)}>
                  <div className="flex items-center space-x-3">
                    <div className={`p-2 rounded-lg ${typeColor[iv.type] || 'bg-gray-100'}`}>
                      <Icon className="w-5 h-5" />
                    </div>
                    <div>
                      <div className="flex items-center space-x-2">
                        <p className="font-medium text-gray-900">{iv.type?.replace('_', ' ')} Interview</p>
                        <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${typeColor[iv.type] || 'bg-gray-100 text-gray-600'}`}>{iv.difficulty || 'MEDIUM'}</span>
                      </div>
                      <p className="text-xs text-gray-500">
                        {iv.createdAt ? new Date(iv.createdAt).toLocaleDateString() : 'Recent'}
                        {iv.topics?.length ? ` · ${iv.topics.slice(0, 3).join(', ')}` : ''}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-3">
                    {iv.status === 'NOT_STARTED' && (
                      <button 
                        onClick={(e) => { e.stopPropagation(); startInterviewSession(iv); }}
                        className="btn btn-primary text-sm"
                      >
                        Start
                      </button>
                    )}
                    {iv.status === 'IN_PROGRESS' && (
                      <button 
                        onClick={(e) => { e.stopPropagation(); startInterviewSession(iv); }}
                        className="btn btn-primary text-sm"
                      >
                        Continue
                      </button>
                    )}
                    {score != null && (
                      <div className="text-right">
                        <p className={`text-xl font-bold ${scoreColor(score)}`}>{score}<span className="text-sm text-gray-400">/10</span></p>
                        <div className="flex items-center justify-end">
                          {[1,2,3,4,5].map(s => <StarIcon key={s} className={`w-3 h-3 ${s <= Math.round(score / 2) ? 'text-yellow-400 fill-yellow-400' : 'text-gray-300'}`} />)}
                        </div>
                      </div>
                    )}
                    {isExp ? <ChevronUpIcon className="w-4 h-4 text-gray-400" /> : <ChevronDownIcon className="w-4 h-4 text-gray-400" />}
                  </div>
                </div>

                {isExp && (
                  <div className="mt-4 pt-4 border-t border-gray-100 space-y-4">
                    {iv.questions?.length > 0 && (
                      <div>
                        <p className="text-xs font-semibold text-gray-500 mb-3">QUESTIONS & ANSWERS</p>
                        <div className="space-y-4">
                          {iv.questions.map((q, qi) => {
                            const ur = iv.userResponses?.find(r => r.questionId === q.id) || {};
                            return (
                              <div key={qi} className="bg-gray-50 rounded-lg p-4">
                                <p className="font-medium text-sm text-gray-800 mb-2"><span className="text-primary-600 mr-1">Q{qi+1}.</span>{q.text}</p>
                                {ur.answer && (
                                  <div className="mb-2">
                                    <p className="text-xs font-medium text-gray-500 mb-1">YOUR ANSWER:</p>
                                    <p className="text-sm text-gray-700 bg-white rounded p-2 border">{ur.answer}</p>
                                  </div>
                                )}
                                {ur.feedback && (
                                  <div>
                                    <p className="text-xs font-medium text-gray-500 mb-1">AI FEEDBACK:</p>
                                    <p className="text-sm text-green-700 bg-green-50 rounded p-2">{ur.feedback}</p>
                                  </div>
                                )}
                                {ur.score != null && <p className="text-xs text-right mt-1 font-medium text-primary-600">Score: {ur.score}/10</p>}
                              </div>
                            );
                          })}
                        </div>
                      </div>
                    )}
                    {iv.overallFeedback && (
                      <div>
                        <p className="text-xs font-semibold text-gray-500 mb-2">OVERALL FEEDBACK</p>
                        <p className="text-sm text-gray-700 bg-blue-50 rounded-lg p-3">{iv.overallFeedback}</p>
                      </div>
                    )}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default MockInterviews;
