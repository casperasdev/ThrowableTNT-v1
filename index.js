const https = require('https');
const fs = require('fs');
const { exec } = require('child_process');

const PAPER_VERSION = '1.21';
const BUILD = 'latest';
const PAPER_JAR = 'paper.jar';

console.log('ThrowableTNT Server Setup');
console.log('=========================\n');

function downloadPaper() {
  return new Promise((resolve, reject) => {
    if (fs.existsSync(PAPER_JAR)) {
      console.log('✓ Paper server already downloaded');
      return resolve();
    }

    console.log(`Downloading Paper ${PAPER_VERSION}...`);
    const url = `https://api.papermc.io/v2/projects/paper/versions/${PAPER_VERSION}/builds`;

    https.get(url, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        const builds = JSON.parse(data).builds;
        const latestBuild = builds[builds.length - 1];
        const downloadUrl = `https://api.papermc.io/v2/projects/paper/versions/${PAPER_VERSION}/builds/${latestBuild.build}/downloads/${latestBuild.downloads.application.name}`;

        const file = fs.createWriteStream(PAPER_JAR);
        https.get(downloadUrl, (dlRes) => {
          dlRes.pipe(file);
          file.on('finish', () => {
            file.close();
            console.log('✓ Paper downloaded successfully\n');
            resolve();
          });
        }).on('error', reject);
      });
    }).on('error', reject);
  });
}

function acceptEula() {
  if (!fs.existsSync('eula.txt')) {
    fs.writeFileSync('eula.txt', 'eula=true');
    console.log('✓ EULA accepted');
  }
}

function createStartScript() {
  const isWindows = process.platform === 'win32';
  const scriptName = isWindows ? 'start.bat' : 'start.sh';
  const scriptContent = isWindows
    ? '@echo off\njava -Xmx2G -Xms2G -jar paper.jar nogui\npause'
    : '#!/bin/bash\njava -Xmx2G -Xms2G -jar paper.jar nogui';

  if (!fs.existsSync(scriptName)) {
    fs.writeFileSync(scriptName, scriptContent);
    if (!isWindows) {
      fs.chmodSync(scriptName, '755');
    }
    console.log(`✓ Created ${scriptName}`);
  }
}

async function setup() {
  try {
    await downloadPaper();
    acceptEula();
    createStartScript();

    console.log('\n=========================');
    console.log('Setup complete!');
    console.log('=========================\n');
    console.log('To start your server:');
    console.log(process.platform === 'win32' ? '  start.bat' : '  ./start.sh');
    console.log('\nThe ThrowableTNT plugin will be loaded from the plugins/ folder');

  } catch (error) {
    console.error('Error during setup:', error);
  }
}

setup();
