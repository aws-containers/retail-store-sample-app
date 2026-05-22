# Chat Feature Setup - Complete Index

Everything you need to set up and debug chat with a custom OpenAI-compatible API.

## 📋 Quick Navigation

### I Just Want to Get It Working (5 minutes)
1. Read: **QUICK_START.md**
2. Run: `./check-config.sh` (verify configuration)
3. Start: `source .env && ./mvnw spring-boot:run`

### I Need Step-by-Step Instructions
1. Read: **SETUP_CUSTOM_API.md** (detailed configuration)
2. Read: **TESTING_CHAT.md** (verify it works)
3. Check: **JAVA_IMPLEMENTATION.md** (understand how it works)

### I Want to Understand the Code
1. Read: **JAVA_IMPLEMENTATION.md** (architecture & code flow)
2. Review: `src/main/java/com/amazon/sample/ui/config/chat/`
3. Review: `src/main/java/com/amazon/sample/ui/web/ChatController.java`

### My Chat Isn't Working
1. Run: `./check-config.sh` (diagnose configuration)
2. Read: **TESTING_CHAT.md** (step-by-step troubleshooting)
3. Check: Application logs for error messages

---

## 📚 Documentation Files

### QUICK_START.md
**Time to read**: 2 minutes  
**For whom**: Anyone who wants to get running immediately  
**Contains**:
- TL;DR copy-paste commands
- Variable mapping reference
- Verification checklist
- Quick troubleshooting

**Start here if**: You just want it working NOW

### SETUP_CUSTOM_API.md
**Time to read**: 10 minutes  
**For whom**: People who want to understand all configuration options  
**Contains**:
- 3 different setup methods (env vars, properties, command line)
- All configuration properties explained
- Docker setup instructions
- Troubleshooting guide
- Security notes

**Start here if**: You want detailed configuration options

### TESTING_CHAT.md
**Time to read**: 15 minutes  
**For whom**: People who need to verify everything is working  
**Contains**:
- Step-by-step testing procedures
- API connectivity testing (curl examples)
- Browser testing procedures
- Different test scenarios
- Performance monitoring
- Troubleshooting for each test

**Start here if**: You need to verify the setup is working correctly

### JAVA_IMPLEMENTATION.md
**Time to read**: 20 minutes  
**For whom**: Developers who want to understand the code  
**Contains**:
- Architecture overview
- Each configuration class explained
- Code examples with comments
- Data flow diagram
- Extension points
- Dependencies

**Start here if**: You want to understand or modify the code

### CONFIG_SUMMARY.md
**Time to read**: 10 minutes  
**For whom**: Reference document for configuration flow  
**Contains**:
- How environment variables flow through the system
- All configuration options in tables
- Validation checklist
- Common problems & solutions
- Testing commands
- Avoid & do's table

**Start here if**: You need a reference document

### README.md
**Time to read**: 5 minutes  
**For whom**: General application info  
**Contains**:
- Application overview
- Environment variables reference
- Running instructions
- Chat configuration section (updated)

**Start here if**: You need general application information

### check-config.sh
**Time to run**: < 1 minute  
**For whom**: Anyone with configuration problems  
**Does**:
- Checks all required environment variables
- Validates URL format
- Tests API connectivity
- Provides actionable error messages

**Run this if**: Chat isn't working (diagnoses the issue)

---

## 🔧 Setting Up - Three Ways

### Method 1: Fastest (Direct)
```bash
cd /Users/abhay/Desktop/Amazon/retail-store-sample-app/src/ui
RETAIL_UI_CHAT_ENABLED=true \
RETAIL_UI_CHAT_PROVIDER=openai \
RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2 \
RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com \
RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-KEY \
./mvnw spring-boot:run
```

### Method 2: Recommended (.env file)
```bash
# Create .env file with your configuration
cat > /Users/abhay/Desktop/Amazon/retail-store-sample-app/src/ui/.env << 'EOF'
RETAIL_UI_CHAT_ENABLED=true
RETAIL_UI_CHAT_PROVIDER=openai
RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-KEY
EOF

# Then run
cd /Users/abhay/Desktop/Amazon/retail-store-sample-app/src/ui
source .env
./mvnw spring-boot:run
```

### Method 3: Properties File
Create `src/main/resources/application-local.yml`:
```yaml
retail:
  ui:
    chat:
      enabled: true
      provider: openai
      model: deepseek/deepseek-v3.2
      openai:
        base-url: https://api.ai.kodekloud.com
        api-key: sk-YOUR-KEY
```

Run with: `./mvnw spring-boot:run --spring.profiles.active=local`

---

## 🧪 Verification Steps

1. **Check configuration**: `./check-config.sh`
2. **View logs**: Look for "Creating OpenAI chat client with baseUrl: ..."
3. **Open browser**: Navigate to `http://localhost:8080`
4. **Find chat widget**: Should be visible in the UI
5. **Send message**: Type something and hit send
6. **Get response**: Should receive AI-generated response

---

## 🐛 Troubleshooting Decision Tree

```
Chat not working?
├─ Run: ./check-config.sh
│  ├─ Variables not set? → See QUICK_START.md, set missing vars
│  ├─ Invalid URL? → Fix base URL format (must be https://...)
│  ├─ API test failed? → See TESTING_CHAT.md #Step2 (test with curl)
│  └─ All passed? → Continue below
│
├─ Check logs for errors
│  ├─ "Cannot connect"? → Network/firewall issue
│  ├─ "401 Unauthorized"? → Invalid API key
│  ├─ "404 Not Found"? → Wrong base URL
│  └─ Other error? → Google the error message
│
├─ Verify application started
│  ├─ "Creating OpenAI chat client"? → Good, continue
│  └─ Not showing? → Chat disabled or config not loaded
│
├─ Check UI
│  ├─ Chat widget visible? → Try sending message
│  └─ Not visible? → Check browser console for JS errors
│
└─ Still broken?
   └─ See TESTING_CHAT.md for comprehensive troubleshooting
```

---

## 📝 Configuration Reference

### Required Variables
| Variable | Example | What it does |
|----------|---------|-------------|
| `RETAIL_UI_CHAT_ENABLED` | `true` | Turns chat on/off |
| `RETAIL_UI_CHAT_PROVIDER` | `openai` | Which service to use |
| `RETAIL_UI_CHAT_MODEL` | `deepseek/deepseek-v3.2` | Which model to use |
| `RETAIL_UI_CHAT_OPENAI_BASE_URL` | `https://api.ai.kodekloud.com` | Your API endpoint |
| `RETAIL_UI_CHAT_OPENAI_API_KEY` | `sk-...` | Your API key |

### Optional Variables
| Variable | Default | What it does |
|----------|---------|-------------|
| `RETAIL_UI_CHAT_TEMPERATURE` | `0.6` | Model creativity (0-1) |
| `RETAIL_UI_CHAT_MAX_TOKENS` | `300` | Max response length |

---

## 🗂️ File Structure

```
src/ui/
├─ README.md                    ← Updated with chat config section
├─ pom.xml                      ← Dependencies already included
├─ QUICK_START.md              ← Start here for quick setup
├─ SETUP_CUSTOM_API.md         ← Detailed configuration
├─ TESTING_CHAT.md             ← How to test
├─ JAVA_IMPLEMENTATION.md      ← How the code works
├─ CONFIG_SUMMARY.md           ← Reference & flow diagrams
├─ CHAT_SETUP_INDEX.md         ← This file
├─ check-config.sh             ← Diagnostic tool
│
├─ src/main/resources/
│  ├─ application.yml          ← Default configuration
│  └─ application-prod.yml     ← Production configuration
│
├─ src/main/java/.../config/chat/
│  ├─ ChatProperties.java      ← Loads general config
│  ├─ OpenAIChatProperties.java ← Loads OpenAI config
│  ├─ OpenAIChatConfig.java    ← Builds ChatClient ✅ SUPPORTS CUSTOM URL
│  ├─ BedrockChatConfig.java   ← AWS Bedrock support
│  └─ MockChatConfig.java      ← Test support
│
└─ src/main/java/.../web/
   └─ ChatController.java      ← REST endpoint /chat/submit
```

---

## ⚡ Performance Tips

1. **Use .env file**: Faster than typing long commands
2. **Cache model responses**: Implement caching for repeated queries
3. **Monitor response times**: Check logs for slow requests
4. **Use appropriate max-tokens**: Lower = faster responses
5. **Adjust temperature**: Lower = faster, more consistent responses

---

## 🔒 Security Reminders

- ✅ **DO**: Use environment variables for secrets
- ✅ **DO**: Use HTTPS for production APIs
- ✅ **DO**: Rotate API keys regularly
- ✅ **DO**: Add `.env` to `.gitignore`
- ✅ **DO**: Monitor API usage

- ❌ **DON'T**: Commit API keys to Git
- ❌ **DON'T**: Share .env files
- ❌ **DON'T**: Use HTTP in production
- ❌ **DON'T**: Log sensitive data

---

## 📞 Getting Help

1. **Quick answer**: Run `./check-config.sh`
2. **Detailed guide**: Read TESTING_CHAT.md
3. **Code question**: Read JAVA_IMPLEMENTATION.md
4. **Configuration**: Read SETUP_CUSTOM_API.md
5. **Still stuck**: Check application logs!

---

## 🎯 Common Workflows

### Workflow 1: First Time Setup
```
1. Read QUICK_START.md (2 min)
2. Create .env file (2 min)
3. Run ./check-config.sh (1 min)
4. Start app: source .env && ./mvnw spring-boot:run
5. Open browser: http://localhost:8080
6. Test chat feature
```

### Workflow 2: Troubleshooting
```
1. Run ./check-config.sh (diagnose)
2. Check application logs
3. Read relevant section in TESTING_CHAT.md
4. Fix configuration
5. Restart application
6. Verify with ./check-config.sh
```

### Workflow 3: Understanding the Code
```
1. Read JAVA_IMPLEMENTATION.md architecture section
2. Review ChatProperties.java
3. Review OpenAIChatProperties.java
4. Review OpenAIChatConfig.java
5. Review ChatController.java
6. Trace the data flow from UI to API
```

---

## 📊 Status at Startup

After starting with correct configuration, you should see:

```
✓ Chat feature enabled
✓ Provider: openai
✓ Model: deepseek/deepseek-v3.2
✓ Base URL: https://api.ai.kodekloud.com
✓ Creating OpenAI chat client with baseUrl: https://api.ai.kodekloud.com
✓ Using custom OpenAI base URL: https://api.ai.kodekloud.com
✓ ChatClient bean created successfully
✓ Application started
✓ Chat endpoint available at: /chat/submit
✓ UI accessible at: http://localhost:8080
```

---

**Ready to start?** → Go to **QUICK_START.md**  
**Need detailed setup?** → Go to **SETUP_CUSTOM_API.md**  
**Having problems?** → Run **./check-config.sh**
