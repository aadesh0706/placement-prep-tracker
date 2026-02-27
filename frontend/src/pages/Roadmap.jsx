import { useState, useEffect } from 'react';
import { roadmapAPI } from '../services/api';
import {
  MapIcon,
  CheckCircleIcon,
  ClockIcon,
  ChevronDownIcon,
  ChevronUpIcon,
  SparklesIcon,
} from '@heroicons/react/24/outline';

const TARGETS = ['Software Engineer', 'Data Analyst', 'Full Stack Developer', 'DevOps Engineer', 'AI/ML Engineer', 'Other'];
const LEVELS = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED'];

const Roadmap = () => {
  const [roadmap, setRoadmap] = useState(null);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [expandedPhase, setExpandedPhase] = useState(null);
  const [form, setForm] = useState({ targetRole: '', currentLevel: 'BEGINNER', skills: '', weeksAvailable: 12, hoursPerWeek: 10 });

  useEffect(() => { fetchRoadmap(); }, []);

  const fetchRoadmap = async () => {
    setLoading(true);
    try {
      const r = await roadmapAPI.getRoadmap();
      setRoadmap(r);
    } catch (e) {
      setRoadmap(null);
    } finally { setLoading(false); }
  };

  const generate = async (e) => {
    e.preventDefault();
    setGenerating(true);
    try {
      const payload = {
        targetRole: form.targetRole,
        currentLevel: form.currentLevel,
        weakAreas: form.skills.split(',').map(s => s.trim()).filter(Boolean),
        weeksAvailable: parseInt(form.weeksAvailable) || 12,
        hoursPerWeek: parseInt(form.hoursPerWeek) || 10,
      };
      const r = await roadmapAPI.generate(payload);
      setRoadmap(r);
      setShowForm(false);
    } catch (err) { alert('Failed to generate roadmap'); }
    finally { setGenerating(false); }
  };

  const togglePhase = async (phaseId, completed) => {
    try {
      await roadmapAPI.updatePhase(roadmap.id, phaseId, { completed });
      setRoadmap(prev => ({
        ...prev,
        phases: prev.phases.map(p => p.id === phaseId ? { ...p, completed } : p),
      }));
    } catch (e) { alert('Failed to update phase'); }
  };

  if (loading) return <div className="flex justify-center p-12"><div className="animate-spin rounded-full h-10 w-10 border-t-2 border-primary-600" /></div>;

  const phases = roadmap?.phases || [];
  const completed = phases.filter(p => p.completed).length;
  const pct = phases.length ? Math.round((completed / phases.length) * 100) : 0;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Study Roadmap</h1>
        <button onClick={() => setShowForm(!showForm)} className="btn btn-primary flex items-center space-x-1">
          <SparklesIcon className="w-4 h-4" /><span>{roadmap ? 'Regenerate' : 'Generate Roadmap'}</span>
        </button>
      </div>

      {showForm && (
        <div className="card border-2 border-primary-200">
          <h2 className="font-semibold mb-4">Generate Personalized Roadmap</h2>
          <form onSubmit={generate} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Target Role</label>
                <select value={form.targetRole} onChange={e => setForm({ ...form, targetRole: e.target.value })}
                  className="input" required>
                  <option value="">Select a role…</option>
                  {TARGETS.map(t => <option key={t}>{t}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Current Level</label>
                <select value={form.currentLevel} onChange={e => setForm({ ...form, currentLevel: e.target.value })} className="input">
                  {LEVELS.map(l => <option key={l}>{l}</option>)}
                </select>
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Weeks Available</label>
                <input type="number" min="4" max="52" value={form.weeksAvailable}
                  onChange={e => setForm({ ...form, weeksAvailable: e.target.value })}
                  className="input" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Hours Per Week</label>
                <input type="number" min="1" max="40" value={form.hoursPerWeek}
                  onChange={e => setForm({ ...form, hoursPerWeek: e.target.value })}
                  className="input" />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Skills You Know (comma-separated)</label>
              <input type="text" value={form.skills} onChange={e => setForm({ ...form, skills: e.target.value })}
                className="input" placeholder="e.g. Java, SQL, React" />
            </div>
            <div className="flex space-x-3">
              <button type="button" onClick={() => setShowForm(false)} className="btn btn-secondary flex-1">Cancel</button>
              <button type="submit" disabled={generating} className="btn btn-primary flex-1">
                {generating ? 'Generating…' : 'Generate'}
              </button>
            </div>
          </form>
        </div>
      )}

      {!roadmap && !showForm && (
        <div className="card text-center py-16">
          <MapIcon className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-gray-700 mb-2">No roadmap yet</h3>
          <p className="text-gray-500 mb-6">Generate a personalized study roadmap based on your target role and skills.</p>
          <button onClick={() => setShowForm(true)} className="btn btn-primary mx-auto">Generate My Roadmap</button>
        </div>
      )}

      {roadmap && (
        <>
          {/* Overview */}
          <div className="card">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h2 className="text-lg font-semibold">{roadmap.targetRole}</h2>
                <p className="text-sm text-gray-500">Level: {roadmap.currentLevel} · Est. {roadmap.estimatedDuration || '?'} weeks</p>
              </div>
              <div className="text-right">
                <p className="text-3xl font-bold text-primary-600">{pct}%</p>
                <p className="text-xs text-gray-400">{completed}/{phases.length} phases</p>
              </div>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-3">
              <div className="bg-primary-600 h-3 rounded-full transition-all" style={{ width: `${pct}%` }} />
            </div>
          </div>

          {/* Phases */}
          <div className="space-y-3">
            {phases.map((phase, i) => (
              <div key={phase.id} className={`card border-l-4 ${phase.completed ? 'border-l-green-500' : 'border-l-primary-400'}`}>
                <div className="flex items-center justify-between cursor-pointer" onClick={() => setExpandedPhase(expandedPhase === phase.id ? null : phase.id)}>
                  <div className="flex items-center space-x-3">
                    <span className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${phase.completed ? 'bg-green-100 text-green-700' : 'bg-primary-100 text-primary-700'}`}>{i + 1}</span>
                    <div>
                      <p className="font-medium text-gray-900">{phase.title}</p>
                      <p className="text-xs text-gray-500 flex items-center"><ClockIcon className="w-3 h-3 mr-1" />{phase.weeksDuration || 1} week{phase.weeksDuration !== 1 ? 's' : ''}</p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-3">
                    <button onClick={e => { e.stopPropagation(); togglePhase(phase.id, !phase.completed); }}
                      className={`flex items-center space-x-1 text-xs px-3 py-1.5 rounded-full font-medium ${phase.completed ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500 hover:bg-green-50 hover:text-green-600'}`}>
                      <CheckCircleIcon className="w-4 h-4" />
                      <span>{phase.completed ? 'Completed' : 'Mark done'}</span>
                    </button>
                    {expandedPhase === phase.id ? <ChevronUpIcon className="w-4 h-4 text-gray-400" /> : <ChevronDownIcon className="w-4 h-4 text-gray-400" />}
                  </div>
                </div>

                {expandedPhase === phase.id && (
                  <div className="mt-4 pt-4 border-t border-gray-100">
                    {phase.description && <p className="text-sm text-gray-600 mb-3">{phase.description}</p>}
                    {phase.topics?.length > 0 && (
                      <div className="mb-3">
                        <p className="text-xs font-semibold text-gray-500 mb-2">TOPICS</p>
                        <div className="flex flex-wrap gap-2">
                          {phase.topics.map((t, ti) => <span key={ti} className="bg-primary-50 text-primary-700 text-xs px-2 py-1 rounded">{t}</span>)}
                        </div>
                      </div>
                    )}
                    {phase.resources?.length > 0 && (
                      <div>
                        <p className="text-xs font-semibold text-gray-500 mb-2">RESOURCES</p>
                        <div className="space-y-1">
                          {phase.resources.map((r, ri) => (
                            <div key={ri} className="flex items-center space-x-2 text-sm">
                              <span className="text-primary-400">▸</span>
                              {r.url ? <a href={r.url} target="_blank" rel="noreferrer" className="text-primary-600 hover:underline">{r.title || r.url}</a>
                                : <span className="text-gray-600">{r.title}</span>}
                              {r.type && <span className="text-xs text-gray-400">({r.type})</span>}
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                )}
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
};

export default Roadmap;
