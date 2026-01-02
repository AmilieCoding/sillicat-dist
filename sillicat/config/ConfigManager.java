package sillicat.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.val;
import sillicat.Sillicat;
import sillicat.module.Module;
import sillicat.module.ModuleManager;
import sillicat.setting.Setting;
import sillicat.setting.impl.BooleanSetting;
import sillicat.setting.impl.ModeSetting;
import sillicat.setting.impl.NumberSetting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ConfigManager {
    private final File configFile;
    private final Gson gson;
    private final ModuleManager moduleManager;

    public ConfigManager(){
        configFile = new File(Sillicat.INSTANCE.getMc().mcDataDir, "sillicat.json");
        gson = new GsonBuilder().setPrettyPrinting().create();
        moduleManager = Sillicat.INSTANCE.getModuleManager();
    }

    public void saveConfig(){
        try{
            ConfigData configData = new ConfigData();
            configData.modules = new ArrayList<>();

            for(Module module : moduleManager.getModules().values()){
                ModuleConfig moduleConfig = new ModuleConfig();
                moduleConfig.name = module.getName();
                moduleConfig.toggled = module.isToggled();
                moduleConfig.key = module.getKey();
                moduleConfig.settings = new ArrayList<>();

                for(Setting setting : module.getSettingList()){
                    SettingConfig settingConfig = new SettingConfig();
                    settingConfig.name = setting.getName();
                    settingConfig.type = setting.getClass().getSimpleName();

                    if(setting instanceof NumberSetting){
                        settingConfig.value = ((NumberSetting) setting).getVal();
                    } else if(setting instanceof BooleanSetting){
                        settingConfig.value = ((BooleanSetting) setting).isValue();
                    } else if(setting instanceof ModeSetting){
                        settingConfig.value = ((ModeSetting) setting).getCurrMode();
                    }

                    moduleConfig.settings.add(settingConfig);
                }

                configData.modules.add(moduleConfig);
            }

            FileWriter writer = new FileWriter(configFile);
            gson.toJson(configData, writer);
            writer.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(){
        try{
            if(!configFile.exists()){
                saveConfig();
                return;
            }

            FileReader reader = new FileReader(configFile);
            Type type = new TypeToken<ConfigData>(){}.getType();
            ConfigData configData = gson.fromJson(reader, type);
            reader.close();

            for(ModuleConfig moduleConfig : configData.modules){
                Module module = moduleManager.getModule(moduleConfig.name);
                if(module != null){
                    if(moduleConfig.key != -1){
                        module.setKey(moduleConfig.key);
                    }
                    module.setEnabled(moduleConfig.toggled);
                    for(SettingConfig settingConfig : moduleConfig.settings){
                        Setting setting = module.getSettingList().stream().filter(s -> s.getName().equalsIgnoreCase(settingConfig.name)).findFirst().orElse(null);
                        if(setting != null){
                            if(setting instanceof NumberSetting && settingConfig.value instanceof Number){
                                ((NumberSetting) setting).setVal(((Number) settingConfig.value).doubleValue());
                            } else if(setting instanceof BooleanSetting && settingConfig.value instanceof Boolean){
                                ((BooleanSetting) setting).setValue((Boolean) settingConfig.value);
                            } else if(setting instanceof ModeSetting && settingConfig.value instanceof String){
                                ((ModeSetting) setting).setCurrMode((String) settingConfig.value);
                            }
                        }
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
