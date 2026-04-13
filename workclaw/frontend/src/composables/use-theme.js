import { ref } from 'vue'

const mode = ref('auto')

function getInitialMode() {
  const stored = localStorage.getItem('theme')
  if (stored === 'light' || stored === 'dark' || stored === 'auto') {
    return stored
  }
  return 'auto'
}

function applyThemeMode(m) {
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  const resolved = m === 'auto' ? (prefersDark ? 'dark' : 'light') : m

  document.documentElement.classList.remove('light', 'dark')
  document.documentElement.classList.add(resolved)
  document.documentElement.style.colorScheme = resolved
}

export function useTheme() {
  const initialMode = getInitialMode()
  mode.value = initialMode
  applyThemeMode(initialMode)

  function toggleMode() {
    const next = mode.value === 'light' ? 'dark' : mode.value === 'dark' ? 'auto' : 'light'
    mode.value = next
    applyThemeMode(next)
    localStorage.setItem('theme', next)
  }

  function setMode(m) {
    mode.value = m
    applyThemeMode(m)
    localStorage.setItem('theme', m)
  }

  return { mode, toggleMode, setMode }
}
