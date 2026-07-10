import fs from 'fs';
import path from 'path';

const buildDir = path.resolve('build');
const indexPath = path.join(buildDir, 'index.html');

if (fs.existsSync(indexPath)) {
  let html = fs.readFileSync(indexPath, 'utf-8');

  // Regex to find the inline script
  const scriptRegex = /<script>([\s\S]*?)<\/script>/;
  const match = html.match(scriptRegex);

  if (match) {
    const inlineScriptContent = match[1];

    // Write the content to bootstrap.js
    const bootstrapPath = path.join(buildDir, 'bootstrap.js');
    fs.writeFileSync(bootstrapPath, inlineScriptContent, 'utf-8');

    // Replace the inline script with a reference to bootstrap.js
    html = html.replace(scriptRegex, '<script src="/bootstrap.js"></script>');
    fs.writeFileSync(indexPath, html, 'utf-8');
    console.log('Successfully extracted inline script to bootstrap.js and updated index.html');
  } else {
    console.warn('No inline script found in index.html');
  }
} else {
  console.error('index.html not found at ' + indexPath);
}
