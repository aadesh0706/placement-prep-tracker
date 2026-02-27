import { useState, useEffect } from 'react';
import { nlpAPI } from '../services/api';
import {
  DocumentTextIcon,
  ArrowUpTrayIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  LightBulbIcon,
  ChartBarIcon,
} from '@heroicons/react/24/outline';

const Resume = () => {
  const [resumes, setResumes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [analyzing, setAnalyzing] = useState(false);
  const [resumeText, setResumeText] = useState('');
  const [fileName, setFileName] = useState('resume.txt');
  const [result, setResult] = useState(null);
  const [tab, setTab] = useState('analyze');

  useEffect(() => { fetchResumes(); }, []);

  const fetchResumes = async () => {
    setLoading(true);
    try { setResumes(await nlpAPI.getResumes()); }
    catch (e) { console.error(e); }
    finally { setLoading(false); }
  };

  const analyze = async () => {
    if (!resumeText.trim()) { alert('Paste your resume text first.'); return; }
    setAnalyzing(true);
    try {
      const r = await nlpAPI.analyzeResume(fileName, resumeText);
      setResult(r);
      fetchResumes();
    } catch (e) { alert('Analysis failed'); }
    finally { setAnalyzing(false); }
  };

  const handleFileUpload = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setFileName(file.name);
    const reader = new FileReader();
    reader.onload = ev => setResumeText(ev.target.result);
    reader.readAsText(file);
  };

  const scoreColor = (s) => {
    if (s >= 80) return 'text-green-600';
    if (s >= 60) return 'text-yellow-600';
    return 'text-red-500';
  };

  const scoreBar = (s) => {
    if (s >= 80) return 'bg-green-500';
    if (s >= 60) return 'bg-yellow-500';
    return 'bg-red-500';
  };

  if (loading) return <div className="flex justify-center p-12"><div className="animate-spin rounded-full h-10 w-10 border-t-2 border-primary-600" /></div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Resume Analysis</h1>
        <div className="flex space-x-2">
          <button onClick={() => setTab('analyze')} className={`px-4 py-2 rounded-lg text-sm font-medium ${tab === 'analyze' ? 'bg-primary-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>Analyze</button>
          <button onClick={() => setTab('history')} className={`px-4 py-2 rounded-lg text-sm font-medium ${tab === 'history' ? 'bg-primary-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>History ({resumes.length})</button>
        </div>
      </div>

      {tab === 'analyze' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Input */}
          <div className="space-y-4">
            <div className="card">
              <h2 className="font-semibold mb-3">Upload or Paste Resume</h2>
              <label className="flex items-center justify-center w-full h-24 border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:border-primary-400 transition-colors mb-3">
                <div className="text-center">
                  <ArrowUpTrayIcon className="w-6 h-6 text-gray-400 mx-auto mb-1" />
                  <span className="text-sm text-gray-500">Upload .txt or .pdf (text-only)</span>
                </div>
                <input type="file" accept=".txt,.pdf" className="hidden" onChange={handleFileUpload} />
              </label>
              <p className="text-xs text-center text-gray-400 mb-3">— or paste text below —</p>
              <textarea
                value={resumeText}
                onChange={e => setResumeText(e.target.value)}
                className="input h-64 resize-none font-mono text-xs"
                placeholder="Paste your resume text here…"
              />
              {fileName && resumeText && (
                <p className="text-xs text-gray-400 mt-1">File: {fileName} · {resumeText.length} chars</p>
              )}
              <button onClick={analyze} disabled={analyzing || !resumeText.trim()} className="btn btn-primary w-full mt-3">
                {analyzing ? 'Analyzing…' : 'Analyze Resume'}
              </button>
            </div>
          </div>

          {/* Result */}
          <div className="space-y-4">
            {!result ? (
              <div className="card text-center py-16">
                <DocumentTextIcon className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                <p className="text-gray-500">Your analysis results will appear here.</p>
              </div>
            ) : (
              <>
                {/* ATS Score */}
                <div className="card text-center">
                  <p className="text-xs font-semibold text-gray-500 mb-2">ATS COMPATIBILITY SCORE</p>
                  <p className={`text-5xl font-extrabold ${scoreColor(result.atsScore)}`}>{result.atsScore}<span className="text-xl text-gray-400">/100</span></p>
                  <div className="w-full bg-gray-200 rounded-full h-3 mt-3">
                    <div className={`h-3 rounded-full ${scoreBar(result.atsScore)}`} style={{ width: `${result.atsScore}%` }} />
                  </div>
                  <p className="text-xs text-gray-500 mt-2">{result.atsScore >= 80 ? 'Excellent ATS compatibility' : result.atsScore >= 60 ? 'Good — room for improvement' : 'Needs significant improvement'}</p>
                </div>

                {/* Skills found */}
                {result.extractedSkills?.length > 0 && (
                  <div className="card">
                    <h3 className="font-semibold text-sm mb-3">Skills Detected</h3>
                    <div className="flex flex-wrap gap-2">
                      {result.extractedSkills.map((s, i) => <span key={i} className="bg-primary-50 text-primary-700 text-xs px-2 py-1 rounded-full">{s}</span>)}
                    </div>
                  </div>
                )}

                {/* Strengths */}
                {result.strengths?.length > 0 && (
                  <div className="card">
                    <h3 className="font-semibold text-sm mb-3 flex items-center"><CheckCircleIcon className="w-4 h-4 text-green-500 mr-1" />Strengths</h3>
                    <ul className="space-y-1">
                      {result.strengths.map((s, i) => <li key={i} className="text-sm text-gray-700 flex items-start"><span className="text-green-500 mr-2 mt-0.5">✓</span>{s}</li>)}
                    </ul>
                  </div>
                )}

                {/* Weaknesses */}
                {result.weaknesses?.length > 0 && (
                  <div className="card">
                    <h3 className="font-semibold text-sm mb-3 flex items-center"><ExclamationTriangleIcon className="w-4 h-4 text-yellow-500 mr-1" />Areas to Improve</h3>
                    <ul className="space-y-1">
                      {result.weaknesses.map((w, i) => <li key={i} className="text-sm text-gray-700 flex items-start"><span className="text-yellow-500 mr-2 mt-0.5">!</span>{w}</li>)}
                    </ul>
                  </div>
                )}

                {/* Suggestions */}
                {result.suggestions?.length > 0 && (
                  <div className="card">
                    <h3 className="font-semibold text-sm mb-3 flex items-center"><LightBulbIcon className="w-4 h-4 text-primary-500 mr-1" />Suggestions</h3>
                    <ul className="space-y-2">
                      {result.suggestions.map((s, i) => <li key={i} className="text-sm text-gray-700 flex items-start"><span className="text-primary-500 mr-2 mt-0.5 font-bold">→</span>{s}</li>)}
                    </ul>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      )}

      {tab === 'history' && (
        <div className="space-y-3">
          {resumes.length === 0 ? (
            <div className="card text-center py-12 text-gray-500">No resume analyses yet.</div>
          ) : (
            resumes.map(r => (
              <div key={r.id} className="card flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <DocumentTextIcon className="w-8 h-8 text-gray-400" />
                  <div>
                    <p className="font-medium text-gray-900">{r.fileName || 'Resume'}</p>
                    <p className="text-xs text-gray-500">{r.uploadedAt ? new Date(r.uploadedAt).toLocaleDateString() : ''} · {r.extractedSkills?.length || 0} skills detected</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className={`text-xl font-bold ${scoreColor(r.atsScore)}`}>{r.atsScore}/100</p>
                  <p className="text-xs text-gray-400">ATS Score</p>
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
};

export default Resume;
