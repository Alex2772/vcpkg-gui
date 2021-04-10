package ru.alex2772.vcpkggui.model;

import java.util.List;

public class InstalledPackageTableModel extends PackageTableModel {
    public InstalledPackageTableModel(List<VcpkgPackage> packages) {
        super(packages);
    }

    @Override
    protected Object getPlatform(VcpkgPackage item) {
        return item.getBuiltPlatform();
    }

}
